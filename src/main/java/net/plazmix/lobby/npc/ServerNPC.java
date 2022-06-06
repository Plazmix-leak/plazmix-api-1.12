package net.plazmix.lobby.npc;

import com.google.common.base.Preconditions;
import lombok.*;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.protocollib.entity.FakeBaseEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public abstract class ServerNPC<E extends FakeBaseEntity> {

    protected final Location location;
    private boolean initialized;

    @Setter(AccessLevel.PUBLIC)
    public E handle;

    @Getter(AccessLevel.PUBLIC)
    public ProtocolHolographic holographic;


    protected abstract void onReceive(@NonNull E entity);

    public E getHandle() {
        if (location == null) {
            return null;
        }

        if (holographic == null) {
            holographic = new SimpleHolographic(location);
        }

        if (handle != null) {
            if (!initialized) {
                initialized = true;

                onReceive(handle);
            }

            return handle;
        }

        return null;
    }

    public void addViewers(@NonNull Player... players) {
        FakeBaseEntity fakeBaseEntity = getHandle();

        if (fakeBaseEntity != null) {
            fakeBaseEntity.addViewers(players);
        }

        if (holographic != null) {
            holographic.addViewers(players);
        }
    }

    public void removeViewers(@NonNull Player... players) {
        FakeBaseEntity fakeBaseEntity = getHandle();

        if (fakeBaseEntity != null) {
            fakeBaseEntity.removeViewers(players);
        }

        if (holographic != null) {
            holographic.removeViewers(players);
        }
    }


    public void addReceivers(@NonNull Player... players) {
        FakeBaseEntity fakeBaseEntity = getHandle();

        if (fakeBaseEntity != null) {
            fakeBaseEntity.addReceivers(players);
        }

        if (holographic != null) {
            holographic.addReceivers(players);
        }
    }

    public void removeReceivers(@NonNull Player... players) {
        FakeBaseEntity fakeBaseEntity = getHandle();

        if (fakeBaseEntity != null) {
            fakeBaseEntity.removeReceivers(players);
        }

        if (holographic != null) {
            holographic.removeReceivers(players);
        }
    }


    protected void addHolographicLine(@NonNull String line) {
        Preconditions.checkArgument(holographic != null, "holographic");

        if (line.replace(" ", "").isEmpty()) {
            holographic.addEmptyHolographicLine();
            return;
        }

        holographic.addOriginalHolographicLine(line);
        holographic.teleport(holographic.getLocation().clone().add(0, 0.25, 0));
    }

    protected void addLocalizedLine(@NonNull String key) {
        Preconditions.checkArgument(holographic != null, "holographic");

        if (key.replace(" ", "").isEmpty()) {
            holographic.addEmptyHolographicLine();
            return;
        }

        holographic.addLangHolographicLine(localizationPlayer -> localizationPlayer.getMessageText(key));
        holographic.teleport(holographic.getLocation().clone().add(0, 0.25, 0));
    }

    protected void removeHolographicLine(int collectionIndex) {
        Preconditions.checkArgument(holographic != null, "holographic");

        ProtocolHolographicLine holographicLine = holographic.getHolographicLine(collectionIndex);
        holographicLine.remove();

        holographic.getHolographicLines().remove(holographicLine);
        holographic.update();

        holographic.teleport(holographic.getLocation().clone().subtract(0, 0.25, 0));
    }

    public void enableAutoLooking(int lookingRadius) {
        E fakeEntity = getHandle();
        Location mainLocation = location.clone();

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : fakeEntity.getViewerCollection()) {

                    if (!player.getLocation().getWorld().equals(mainLocation.getWorld())) {
                        continue;
                    }

                    if (player.getLocation().distance(mainLocation) > lookingRadius) {
                        fakeEntity.look(player, mainLocation.getYaw(), mainLocation.getPitch());
                        continue;
                    }

                    fakeEntity.look(player, player.getLocation());
                }
            }

        }.runTaskTimer(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), 0, 2);
    }
    
    public void enableAutoLooking() {
        enableAutoLooking(10);
    }

}