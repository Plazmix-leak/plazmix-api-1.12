package net.plazmix.utility;

import lombok.NonNull;
import net.minecraft.server.v1_12_R1.EntityFireworks;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public final class FireworkExplosion
        extends EntityFireworks {

    private final Player[] players;
    private boolean gone;

    private FireworkExplosion(@NonNull World world, @NonNull Player... players) {
        super(world);

        this.players = players;
        this.a(0.25F, 0.25F);
    }

    @Override
    public void B_() {
        if (gone) {
            return;
        }

        if (!this.world.isClientSide) {
            gone = true;

            if (players != null)
                if (players.length > 0)
                    for (Player player : players)
                        (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
                else
                    world.broadcastEntityEffect(this, (byte) 17);
            this.die();
        }
    }

    public static void spawn(@NonNull Location location, @NonNull FireworkEffect effect, @NonNull Player... players) {
        try {
            FireworkExplosion craftbukkitFirework = new FireworkExplosion(((CraftWorld) location.getWorld()).getHandle(), players);
            Firework firework = ((Firework) craftbukkitFirework.getBukkitEntity());

            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(effect);
            firework.setFireworkMeta(meta);

            craftbukkitFirework.setPosition(location.getX(), location.getY(), location.getZ());

            if ((((CraftWorld) location.getWorld()).getHandle()).addEntity(craftbukkitFirework)) {
                craftbukkitFirework.setInvisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}