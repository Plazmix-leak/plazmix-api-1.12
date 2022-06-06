package net.plazmix.holographic;

import net.plazmix.protocollib.entity.impl.FakeArmorStand;
import org.bukkit.Location;

public interface ProtocolHolographicLine extends ProtocolHolographicSpawnable {

    int getLineIndex();

    Location getLocation();

    ProtocolHolographic getHolographic();

    FakeArmorStand getFakeArmorStand();


    String getLineText();

    boolean isLocalized();

    void setLineText(String lineText);


    void initialize();

    void update();

    void remove();


    void teleport(Location location);
}
