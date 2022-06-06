package net.plazmix.command.impl;

import net.plazmix.command.BaseCommand;
import net.plazmix.spacepass.SpacePassRewardsRegistry;
import org.bukkit.entity.Player;

public final class SpacePassCommand extends BaseCommand<Player> {

    public SpacePassCommand() {
        super("spacepass", "spass", "pass");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        SpacePassRewardsRegistry.openPassMenu(player);
    }

}
