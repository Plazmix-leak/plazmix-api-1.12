package net.plazmix.lobby.playertop;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.*;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.protocollib.entity.impl.FakeGiantZombie;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class PlayerTopsSkull {

    private final Location location;
    private FakeGiantZombie fakeGiantZombie;

    @Setter
    private Particle particle;


    public FakeGiantZombie create(@NonNull String skullTexture) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        BlockFace blockFace = LocationUtil.yawToFace(location.getYaw(), false);
        Location giantLocation = location.clone();

        switch (blockFace.getOppositeFace()) {
            case SOUTH: {
                giantLocation = location.clone().add(-1.6, -8, -4.5);
                break;
            }

            case EAST: {
                giantLocation = location.clone().add(-4.5, -8, 1.5);
                break;
            }

            case NORTH: {
                giantLocation = location.clone().add(-1.6, -8, 4.5);
                break;
            }

            case WEST: {
                giantLocation = location.clone().add(4.5, -8, -1.5);
                break;
            }
        }

        FakeGiantZombie fakeGiantZombie = new FakeGiantZombie(giantLocation);
        fakeGiantZombie.setInvisible(true);

        fakeGiantZombie.getEntityEquipment().setEquipment(EnumWrappers.ItemSlot.OFFHAND,
                ItemUtil.newBuilder(Material.SKULL_ITEM).setDurability(3).setTextureValue(skullTexture).build());

        // Add particle effects.
        new BukkitRunnable() {

            private final double radian = Math.pow(2, 4);
            private final double radius = 1.5;

            private double t = 0;

            @Override
            public void run() {
                fakeGiantZombie.look( fakeGiantZombie.getLocation().getBlock().getRelative(blockFace.getOppositeFace(), 10).getLocation() );

                if (particle != null) {
                    t += Math.PI / radian;

                    if (t >= Math.PI * 2) {
                        t = 0;
                    }

                    double x = radius * Math.cos(t);
                    double z = radius * Math.sin(t);

                    Location particleLocation = location.clone().add(x, 1, z);

                    for (Player player : fakeGiantZombie.getViewerCollection()) {
                        player.spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0);
                    }
                }
            }

        }.runTaskTimer(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), 0, 1);
        return this.fakeGiantZombie = fakeGiantZombie;
    }

}
