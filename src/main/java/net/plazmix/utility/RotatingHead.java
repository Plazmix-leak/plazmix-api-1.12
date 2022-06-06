package net.plazmix.utility;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.protocollib.entity.impl.FakeArmorStand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class RotatingHead {

    public enum RotateDirection {

        TO_RIGHT,
        TO_LEFT,
        ;
    }

    private final @NonNull Plugin plugin;

    private final @NonNull Location location;
    private final @NonNull String texture;

    private final FakeArmorStand fakeArmorStand;
    private final SimpleHolographic holographic;
    private BukkitRunnable bukkitRunnable;

    private RotateDirection rotateDirection     = RotateDirection.TO_LEFT;
    private float rotatingSpeed                 = 1f;


    public RotatingHead(@NonNull Plugin plugin, @NonNull Location location, @NonNull String texture) {
        this.plugin = plugin;
        this.location = location;
        this.texture = texture;

        this.holographic = new SimpleHolographic(location.clone().subtract(0d, .5d, 0d));
        this.fakeArmorStand = new FakeArmorStand(location.clone().subtract(0d, 1d, 0d));
    }

    public void register() {
        fakeArmorStand.setInvisible(true);

        fakeArmorStand.look(90f, 0f);
        fakeArmorStand.getEntityEquipment()
                .setEquipment(EnumWrappers.ItemSlot.HEAD, ItemUtil.getSkullByTexture(texture));

        holographic.spawn();
        fakeArmorStand.spawn();

        startRotateAnimation(fakeArmorStand);
    }

    public void unregister() {
        fakeArmorStand.remove();
        holographic.remove();

        if (bukkitRunnable != null) {
            bukkitRunnable.cancel();
        }
    }


    public RotatingHead setRotateDirection(@NonNull RotateDirection direction) {
        this.rotateDirection = direction;
        return this;
    }

    public RotatingHead setRotateSpeed(float rotatingSpeed) {
        this.rotatingSpeed = Math.min(10f, rotatingSpeed);
        return this;
    }

    public RotatingHead addTextLine(@NonNull String line) {
        holographic.addOriginalHolographicLine(ChatColor.translateAlternateColorCodes('&', line));
        return this;
    }

    public RotatingHead addEmptyLine() {
        holographic.addEmptyHolographicLine();
        return this;
    }

    public RotatingHead addDropLine(@NonNull ItemStack itemStack) {
        holographic.addItemHolographicLine(itemStack);
        return this;
    }

    public RotatingHead setClickAction(@NonNull Consumer<Player> clickAction) {
        fakeArmorStand.setClickAction(clickAction);

        for (ProtocolHolographicLine holographicLine : holographic.getHolographicLines()) {
            FakeArmorStand holographicLineStand = holographicLine.getFakeArmorStand();

            if (holographicLineStand != null) {
                holographicLineStand.setClickAction(clickAction);
            }
        }

        return this;
    }


    private void startRotateAnimation(FakeArmorStand armorStand) {
        bukkitRunnable = new BukkitRunnable() {
            private float headRotation = rotateDirection == RotateDirection.TO_LEFT ? 0f : 360f;

            @Override
            public void run() {
                armorStand.setHeadRotation(0f, headRotation, 0f);

                switch (rotateDirection) {

                    case TO_LEFT: {
                        headRotation += (rotatingSpeed <= 0f ? 1f : rotatingSpeed * 5f);

                        if (headRotation >= 360f) {
                            headRotation = 0f;
                        }
                        break;
                    }

                    case TO_RIGHT: {
                        headRotation -= (rotatingSpeed <= 0f ? 1f : rotatingSpeed * 5f);

                        if (headRotation <= 0f) {
                            headRotation = 360f;
                        }
                        break;
                    }
                }
            }

        };

        bukkitRunnable.runTaskTimer(plugin, 1, 1);
    }

}
