package net.plazmix.protocollib.entity.impl;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.protocollib.entity.FakeBaseEntityMob;
import net.plazmix.protocollib.entity.FakeEntityMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter
public class FakeCreeper extends FakeBaseEntityMob
        implements FakeEntityMob {

    private boolean ignited;
    private boolean charged;

    public FakeCreeper(@NonNull Location location) {
        super(EntityType.CREEPER, location);
    }

    public void setIgnited(boolean ignited) {
        if (this.ignited == ignited) {
            return;
        }

        this.ignited = ignited;
        broadcastDataWatcherObject(14, BOOLEAN_SERIALIZER, ignited);
    }

    public void setCharged(boolean charged) {
        if (this.charged == charged) {
            return;
        }

        this.charged = charged;
        broadcastDataWatcherObject(13, BOOLEAN_SERIALIZER, charged);
    }

}
