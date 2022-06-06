package net.plazmix.protocollib.entity.impl;

import lombok.NonNull;
import net.plazmix.protocollib.entity.FakeBaseEntityMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeGiantZombie extends FakeBaseEntityMob {

    public FakeGiantZombie(@NonNull Location location) {
        super(EntityType.GIANT, location);
    }

}
