package net.plazmix.protocollib.entity.impl;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.protocollib.entity.FakeBaseEntityMob;
import net.plazmix.protocollib.entity.FakeEntityMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter
public class FakeGuardian extends FakeBaseEntityMob
        implements FakeEntityMob {

    private boolean retractingSpikes;

    public FakeGuardian(@NonNull Location location) {
        super(EntityType.GUARDIAN, location);
    }

    public void setRetractingSpikes(boolean retractingSpikes) {
        if (this.retractingSpikes == retractingSpikes) {
            return;
        }

        this.retractingSpikes = retractingSpikes;
        broadcastDataWatcherObject(12, BOOLEAN_SERIALIZER, retractingSpikes);
    }
}
