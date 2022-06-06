package net.plazmix.protocollib.entity.impl;

import lombok.Getter;
import net.plazmix.protocollib.entity.FakeBaseEntityMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeSlime extends FakeBaseEntityMob {

    @Getter
    private int size;

    public FakeSlime(Location location) {
        super(EntityType.SLIME, location);
    }


    /**
     * Установить новый размер для слайма
     *
     * @param size - новый размер
     */
    public synchronized void setSize(int size) {
        this.size = size;

        broadcastDataWatcherObject(12, INT_SERIALIZER, size);
    }

}
