package net.plazmix.game.command;

import net.plazmix.command.BaseCommand;
import net.plazmix.core.PlazmixCoreApi;
import org.bukkit.entity.Player;

public final class LeaveCommand
        extends BaseCommand<Player> {

    public LeaveCommand() {
        super("leave", "quit", "покинуть", "выйти");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        PlazmixCoreApi.redirectToLobby(player);
    }

}
