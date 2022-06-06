package net.plazmix.protocollib.entity.impl;

import lombok.Getter;
import net.plazmix.protocollib.entity.FakeBaseEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter
public class FakeBat extends FakeBaseEntityLiving {

    private boolean hanging;

    public FakeBat(Location location) {
        super(EntityType.BAT, location);
    }

    public synchronized void setHanging(boolean hanging) {
        this.hanging = hanging;

        broadcastDataWatcherObject(12, BYTE_SERIALIZER, generateBitMask());
    }

    public synchronized byte generateBitMask() {
        return (byte) (hanging ? 0x01 : 0);
    }

}
