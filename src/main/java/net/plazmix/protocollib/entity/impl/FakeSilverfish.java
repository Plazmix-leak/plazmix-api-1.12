package net.plazmix.protocollib.entity.impl;

import net.plazmix.protocollib.entity.FakeBaseEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeSilverfish extends FakeBaseEntityLiving {

    public FakeSilverfish(Location location) {
        super(EntityType.SILVERFISH, location);
    }

}
