package net.plazmix.game.installer;

import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class GameInstallCommand extends BaseCommand<Player> {

    private final GameInstallerConfiguration installerConfiguration;

    public GameInstallCommand(GameInstallerConfiguration installerConfiguration) {
        super("install");
        this.installerConfiguration = installerConfiguration;

        setMinimalGroup(Group.DEVELOPER);
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length < 2) {

            player.sendMessage("§cдаун ебаный, пиши правильно - /install <название карты> <радиус карты>");
            player.sendMessage("§cно для начала встань в центральную точку карты :c");
            return;
        }

        int distance = Integer.parseInt(args[1]);
        String map = args[0];

        installerConfiguration.createIfNotExists(false);

        installerConfiguration.getLoadedConfiguration().set("Map", map);
        installerConfiguration.getLoadedConfiguration().set("InstallRadius", distance);

        installerConfiguration.saveConfiguration();


        Location location = LocationUtil.centralizeXYZ(true, player.getLocation());

        player.getWorld().setSpawnLocation((int)location.getX(), (int)location.getY(), (int)location.getZ());
        player.sendMessage(ChatColor.GREEN + "Все успешно установлено, перезапускаю сервер");

        Bukkit.shutdown();
    }

}
