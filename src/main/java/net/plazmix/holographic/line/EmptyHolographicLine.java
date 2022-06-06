package net.plazmix.holographic.line;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.protocollib.entity.impl.FakeArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class EmptyHolographicLine implements ProtocolHolographicLine {

    private final int lineIndex;

    protected final boolean localized = false;

    private final ProtocolHolographic holographic;

    private Location location;


    public EmptyHolographicLine(int lineIndex, ProtocolHolographic holographic) {
        this.lineIndex = lineIndex;
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
        this.location = location.clone().add(0, -(0.25 * lineIndex), 0);
    }

    @Override
    public void update() { }

    @Override
    public boolean hasReceiver(@NonNull Player player) {
        return true;
    }

    @Override
    public void addReceivers(@NonNull Player... receivers) { }

    @Override
    public void removeReceivers(@NonNull Player... receivers) { }

    @Override
    public boolean hasViewer(@NonNull Player player) {
        return true;
    }

    @Override
    public void addViewers(@NonNull Player... viewers) { }

    @Override
    public void removeViewers(@NonNull Player... viewers) { }

    @Override
    public void spawn() { }

    @Override
    public void remove() { }

    @Override
    public void teleport(Location location) {
        this.location = location;

        initialize();
    }

    @Override
    public FakeArmorStand getFakeArmorStand() {
        return null;
    }

}
