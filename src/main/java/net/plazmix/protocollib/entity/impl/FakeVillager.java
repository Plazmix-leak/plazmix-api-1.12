package net.plazmix.protocollib.entity.impl;

import net.plazmix.protocollib.entity.FakeBaseEntityBabbie;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeVillager extends FakeBaseEntityBabbie {

    public FakeVillager(Location location) {
        super(EntityType.VILLAGER, location);
    }

    public synchronized void setProfession(Profession profession) {
        broadcastDataWatcherObject(13, INT_SERIALIZER, profession.ordinal());
    }

    public synchronized int getProfession() {
        return getDataWatcher().getInteger(13);
    }

    public enum Profession {

        FARMER,
        LIBRARIAN,
        PRIEST,
        BACKSMITH,
        BUTCHER,
        NITWIT
        ;
    }

}
