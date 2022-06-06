package net.plazmix.game.command;

import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.state.GameState;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.player.LocalizationPlayer;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class StartCommand
        extends BaseCommand<Player> {

    public StartCommand() {
        super("start");
        setMinimalGroup(Group.QA);
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        PlazmixUser user = PlazmixUser.of(player);
        Group userGroup = user.getGroup();
        if (Group.QA.getLevel() < userGroup.getLevel() && userGroup.getLevel() < Group.ADMIN.getLevel()) {
            LocalizationPlayer localizedPlayer = user.localization();

            localizedPlayer.sendMessage(localizationResource -> localizationResource.getMessage("MINIMAL_GROUP")
                    .replace("%group%", Group.QA.getColouredName()).toText());

            return;
        }

        GamePlugin gamePlugin = GamePlugin.getInstance();
        GameState currentState = gamePlugin.getService().getCurrentState();

        if (!gamePlugin.getService().getStateManager().getGameStates().getFirst().equals(currentState) || !currentState.isAvailableJoinPlayers()) {
            player.sendMessage(ChatColor.RED + "Ошибка, невозможно запустить игру, пока активна стадия \"" + currentState.getStateName() + "\"");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < 2) {
            player.sendMessage(ChatColor.RED + "Ошибка, для старта нужно как минимум 2 игрока!");
            return;
        }

        gamePlugin.getService().getStateManager().nextStage();
        gamePlugin.broadcastMessage("§d§lPlazmix §8:: " + PlayerUtil.getDisplayName(player) + " §fпринудительно запустил игру!");
    }

}
