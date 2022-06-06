package net.plazmix.holographic.line;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.protocollib.entity.impl.FakeArmorStand;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class HeadHolographicLine implements ProtocolHolographicLine {

    protected final int lineIndex;

    protected final boolean localized = false;

    protected final boolean small;

    protected final ProtocolHolographic holographic;

    protected Location location;


    @Setter
    protected String lineText;


    @Setter
    private FakeArmorStand fakeArmorStand;


    public HeadHolographicLine(int lineIndex, String skullTexture, boolean small, ProtocolHolographic holographic) {
        this.lineIndex = lineIndex;
        this.holographic = holographic;
        this.small = small;
        this.lineText = skullTexture;

        this.location = holographic.getLocation();
    }


    @Override
    public void initialize() {
        setFakeArmorStand(new FakeArmorStand(getLocation().clone().add(0, -(0.2 * lineIndex), 0)));

        if (lineText.length() > 20) {
            fakeArmorStand.getEntityEquipment()
                    .setEquipment(EnumWrappers.ItemSlot.HEAD, ItemUtil.getSkullByTexture(lineText));
        } else {
            fakeArmorStand.getEntityEquipment()
                    .setEquipment(EnumWrappers.ItemSlot.HEAD, ItemUtil.getSkull(lineText));
        }

        fakeArmorStand.setSmall(small);
        fakeArmorStand.setInvisible(true);
        fakeArmorStand.setBasePlate(false);

        //для расстояния между головой и новыми строками
        holographic.addEmptyHolographicLine();
        holographic.addEmptyHolographicLine();

        if (!fakeArmorStand.isSmall()) {
            holographic.addEmptyHolographicLine();
        }
    }

    @Override
    public void update() {
        fakeArmorStand.getEntityEquipment()
                .setEquipment(EnumWrappers.ItemSlot.HEAD, ItemUtil.getSkullByTexture(lineText));
    }

    @Override
    public boolean hasReceiver(@NonNull Player player) {
        return fakeArmorStand.hasReceiver(player);
    }

    @Override
    public void addReceivers(@NonNull Player... receivers) {
        fakeArmorStand.addReceivers(receivers);
    }

    @Override
    public void removeReceivers(@NonNull Player... receivers) {
        fakeArmorStand.removeReceivers(receivers);
    }

    @Override
    public boolean hasViewer(@NonNull Player player) {
        return fakeArmorStand.hasViewer(player);
    }

    @Override
    public void addViewers(@NonNull Player... viewers) {
        fakeArmorStand.addViewers(viewers);
    }

    @Override
    public void removeViewers(@NonNull Player... viewers) {
        fakeArmorStand.removeViewers(viewers);
    }

    @Override
    public void spawn() {
        fakeArmorStand.spawn();
    }

    @Override
    public void remove() {
        fakeArmorStand.remove();
    }

    @Override
    public void teleport(Location location) {
        this.location = location.clone().add(0, -(0.25 * lineIndex), 0);

        fakeArmorStand.teleport(this.location);
    }

}
