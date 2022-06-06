package net.plazmix.holographic.line;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.protocollib.entity.impl.FakeArmorStand;
import net.plazmix.protocollib.entity.impl.FakeDroppedItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class ItemHolographicLine implements ProtocolHolographicLine {

    protected final int lineIndex;

    protected final boolean localized = false;

    protected final ProtocolHolographic holographic;

    protected Location location;

    @Setter
    private FakeDroppedItem fakeDroppedItem;

    @Setter
    protected ItemStack itemStack;


    public ItemHolographicLine(int lineIndex, ItemStack itemStack, ProtocolHolographic holographic) {
        this.lineIndex = lineIndex;
        this.itemStack = itemStack;
        this.holographic = holographic;

        this.location = holographic.getLocation();
    }


    @Override
    public String getLineText() {
        return "";
    }

    @Override
    public void setLineText(String lineText) { }


    @Override
    public void initialize() {
        holographic.addEmptyHolographicLine();

        fakeDroppedItem = new FakeDroppedItem(itemStack, getLocation().clone().add(0, -(0.25 * lineIndex) + 1.9, 0));
        fakeDroppedItem.setNoGravity(true);
    }

    @Override
    public void update() {
        fakeDroppedItem.setItemStack(itemStack);
    }

    @Override
    public boolean hasReceiver(@NonNull Player player) {
        return fakeDroppedItem.hasReceiver(player);
    }

    @Override
    public void addReceivers(@NonNull Player... receivers) {
        fakeDroppedItem.addReceivers(receivers);
    }

    @Override
    public void removeReceivers(@NonNull Player... receivers) {
        fakeDroppedItem.removeReceivers(receivers);
    }

    @Override
    public boolean hasViewer(@NonNull Player player) {
        return fakeDroppedItem.hasViewer(player);
    }

    @Override
    public void addViewers(@NonNull Player... viewers) {
        fakeDroppedItem.addViewers(viewers);
    }

    @Override
    public void removeViewers(@NonNull Player... viewers) {
        fakeDroppedItem.removeViewers(viewers);
    }

    @Override
    public void spawn() {
        fakeDroppedItem.spawn();
    }

    @Override
    public void remove() {
        fakeDroppedItem.remove();
    }

    @Override
    public void teleport(Location location) {
        this.location = location.clone().add(0, -(0.25 * lineIndex) + 1.9, 0);

        fakeDroppedItem.teleport(this.location);
    }

    @Override
    public FakeArmorStand getFakeArmorStand() {
        return null;
    }

}
