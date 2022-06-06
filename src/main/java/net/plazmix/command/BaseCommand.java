package net.plazmix.command;

import lombok.Getter;
import lombok.Setter;
import net.plazmix.command.manager.CommandManager;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.utility.player.LocalizationPlayer;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class BaseCommand<S extends CommandSender>
        extends Command
        implements CommandExecutor {

    @Setter
    private Group minimalGroup = Group.ABOBA;

    @Setter
    private TabCompleter tabCompleter;


    /**
     * На тот случай, если при регистрации команды
     * нужно указывать только 1 алиас
     *
     * @param command - алиас
     */
    public BaseCommand(String command) {
        this(command, new String[0]);
    }

    /**
     * На тот случай, если у команды несколько
     * вариаций алиасов
     *
     * @param command - главный алиас
     * @param aliases - алиасы
     */
    public BaseCommand(String command, String... aliases) {
        this(false, command, aliases);
    }

    /**
     * На тот случай, если у команды несколько
     * вариаций алиасов
     *
     * @param command - главный алиас
     * @param aliases - алиасы
     */
    public BaseCommand(boolean constructorRegister, String command, String... aliases) {
        super(command, "The command is registered by PlazmixAPI", ("/").concat(command), Arrays.asList(aliases));

        if (constructorRegister) {
            CommandManager.INSTANCE.registerCommand(this, command, aliases);
        }
    }

    @SuppressWarnings("all")
    protected final Class<S> getSenderType() {
        return (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        Class<S> senderClass = getSenderType();

        if (!senderClass.isAssignableFrom(CommandSender.class)) {
            boolean senderIsPlayer = senderClass.isAssignableFrom(Player.class);

            if (!(commandSender instanceof Player) && senderIsPlayer) {
                return true;
            }

            if (commandSender instanceof Player && !senderIsPlayer) {
                return true;
            }
        }

        if (commandSender instanceof Player) {
            PlazmixUser plazmixUser = PlazmixUser.of(commandSender.getName());

            if (plazmixUser.getGroup().getLevel() < minimalGroup.getLevel()) {
                LocalizationPlayer localizedPlayer = plazmixUser.localization();

                localizedPlayer.sendMessage(localizationResource -> localizationResource.getMessage("MINIMAL_GROUP")
                        .replace("%group%", minimalGroup.getColouredName()).toText());

                return true;
            }
        }

        onExecute((S) commandSender, args);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
        if (tabCompleter == null) {
            return super.tabComplete(sender, alias, args);
        }

        return tabCompleter.onTabComplete(sender, this, alias, args);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        return true;
    }

    /**
     * execute команды
     *
     * @param commandSender - отправитель
     * @param args - аргументы команды
     */
    protected abstract void onExecute(S commandSender, String[] args);
}
