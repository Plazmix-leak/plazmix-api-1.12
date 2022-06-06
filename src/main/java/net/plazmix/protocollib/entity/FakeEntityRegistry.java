package net.plazmix.protocollib.entity;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class FakeEntityRegistry {

    public static final FakeEntityRegistry INSTANCE = new FakeEntityRegistry();

    protected final TIntObjectMap<FakeEntity> fakeEntityIdsMap = new TIntObjectHashMap<>();


    public synchronized FakeEntity getEntityById(int entityId) {
        return fakeEntityIdsMap.get(entityId);
    }

    public synchronized void registerEntity(@NonNull FakeEntity fakeEntity) {
        if (hasEntity(fakeEntity)) {
            return;
        }

        fakeEntityIdsMap.put(fakeEntity.getEntityId(), fakeEntity);
    }

    public synchronized void unregisterEntity(@NonNull FakeEntity fakeEntity) {
        if (!hasEntity(fakeEntity)) {
            return;
        }

        fakeEntityIdsMap.remove(fakeEntity.getEntityId());
    }

    public synchronized boolean hasEntity(@NonNull FakeEntity fakeEntity) {
        return fakeEntityIdsMap.containsKey(fakeEntity.getEntityId());
    }


    public synchronized Collection<FakeEntity> getEntitiesByScope(@NonNull FakeEntityScope fakeEntityScope) {
        return fakeEntityIdsMap.valueCollection()
                .stream()
                .filter(fakeBaseEntity -> fakeBaseEntity.getEntityScope().equals(fakeEntityScope))
                .collect(Collectors.toSet());
    }

    public synchronized Collection<FakeEntity> getReceivableEntities(@NonNull Player player) {
        Collection<FakeEntity> receivableEntities = new ArrayList<>();

        for (FakeEntity fakeEntity : fakeEntityIdsMap.valueCollection()) {

            if (fakeEntity.getEntityScope().equals(FakeEntityScope.PUBLIC) || fakeEntity.hasReceiver(player)) {
                receivableEntities.add(fakeEntity);
            }
        }

        return receivableEntities;
    }

    public synchronized Collection<FakeEntity> getViewableEntities(@NonNull Player player) {
        Collection<FakeEntity> viewableEntities = new ArrayList<>();

        for (FakeEntity fakeEntity : fakeEntityIdsMap.valueCollection()) {

            if (fakeEntity.hasViewer(player)) {
                viewableEntities.add(fakeEntity);
            }
        }

        return viewableEntities;
    }

}
