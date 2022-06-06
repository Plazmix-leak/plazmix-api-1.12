package net.plazmix.command.impl;

import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkCommand extends BaseCommand<Player> {

    public FireworkCommand() {
        super("firework", "fw", "fwork", "firew");

        setMinimalGroup(Group.STAR);
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.setPower(2);
        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)

                .withColor(Color.PURPLE)
                .withColor(Color.AQUA)
                .withColor(Color.YELLOW)

                .build());

        firework.setFireworkMeta(fireworkMeta);
    }

}
