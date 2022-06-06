package net.plazmix.protocollib.entity;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import net.plazmix.protocollib.entity.equipment.FakeEntityEquipment;
import net.plazmix.protocollib.packet.AbstractPacket;
import net.plazmix.protocollib.packet.ProtocolPacketFactory;
import net.plazmix.protocollib.packet.entity.*;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Getter
public abstract class FakeBaseEntity implements Cloneable, FakeEntity {

    public static @NonNull WrappedDataWatcher.Serializer BYTE_SERIALIZER        = WrappedDataWatcher.Registry.get(Byte.class);
    public static @NonNull WrappedDataWatcher.Serializer FLOAT_SERIALIZER       = WrappedDataWatcher.Registry.get(Float.class);
    public static @NonNull WrappedDataWatcher.Serializer INT_SERIALIZER         = WrappedDataWatcher.Registry.get(Integer.class);
    public static @NonNull WrappedDataWatcher.Serializer STRING_SERIALIZER      = WrappedDataWatcher.Registry.get(String.class);
    public static @NonNull WrappedDataWatcher.Serializer BOOLEAN_SERIALIZER     = WrappedDataWatcher.Registry.get(Boolean.class);

    public static @NonNull WrappedDataWatcher.Serializer ITEMSTACK_SERIALIZER   = WrappedDataWatcher.Registry.get(MinecraftReflection.getItemStackClass());
    public static @NonNull WrappedDataWatcher.Serializer ROTATION_SERIALIZER    = WrappedDataWatcher.Registry.get(Vector3F.getMinecraftClass());

    public static @NonNull FieldAccessor ENTITY_ID_ACCESSOR = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);


    protected @NonNull int entityId;
    protected @NonNull int spawnTypeId;

    protected @NonNull EntityType entityType;

    protected FakeEntityScope entityScope;

    protected String customName;
    protected Location location;

    protected ChatColor glowingColor;

    protected @NonNull FakeEntityEquipment entityEquipment      = new FakeEntityEquipment(this);
    protected @NonNull WrappedDataWatcher dataWatcher           = new WrappedDataWatcher();

    protected @NonNull Collection<Player> viewerCollection      = new LinkedHashSet<>();
    protected @NonNull Collection<Player> receiverCollection    = new LinkedHashSet<>();

    protected boolean burning;
    protected boolean sneaking;
    protected boolean sprinting;
    protected boolean invisible;
    protected boolean elytraFlying;
    protected boolean customNameVisible;
    protected boolean noGravity;

    @Setter protected Consumer<Player> clickAction;
    @Setter protected Consumer<Player> attackAction;


    public FakeBaseEntity(int entityId, @NonNull EntityType entityType, @NonNull Location location, @NonNull FakeEntityScope entityScope) {
        this.entityId = entityId;
        this.entityType = entityType;

        this.location = location;
        this.entityScope = entityScope;
    }

    public FakeBaseEntity(@NonNull EntityType entityType, @NonNull Location location, @NonNull FakeEntityScope entityScope) {
        this(ENTITY_ID_ACCESSOR.get(null) instanceof AtomicInteger ? ((AtomicInteger) ENTITY_ID_ACCESSOR.get(null)).incrementAndGet() : ((int) ENTITY_ID_ACCESSOR.get(null)),
                entityType, location, entityScope);

        if (FakeEntityRegistry.INSTANCE.hasEntity(this)) {
            return;
        }

        // 1.13+ use final AtomicInteger
        if (!(ENTITY_ID_ACCESSOR.get(null) instanceof AtomicInteger)) {
            ENTITY_ID_ACCESSOR.set(null, entityId + 1);
        }
    }

    public FakeBaseEntity(@NonNull EntityType entityType, @NonNull Location location) {
        this(entityType, location, FakeEntityScope.PROTOTYPE);
    }


    @Override
    public synchronized void setEntityScope(@NonNull FakeEntityScope entityScope) {
        this.entityScope = entityScope;
    }

    @Override
    public World getWorld() {
        return location.getWorld();
    }

    @Override
    public Chunk getChunk() {
        return location.getChunk();
    }

    @Override
    public synchronized void spawn() {
        spawn(true);
    }

    public synchronized void spawn(boolean isPublic) {
        if (isPublic) setEntityScope(FakeEntityScope.PUBLIC);

        FakeEntityRegistry.INSTANCE.registerEntity(this);
        addReceivers(Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    @Override
    public synchronized void remove() {
        setEntityScope(FakeEntityScope.PROTOTYPE);

        FakeEntityRegistry.INSTANCE.unregisterEntity(this);
        removeReceivers(receiverCollection.toArray(new Player[0]));
    }

    @Override
    public synchronized boolean hasReceiver(@NonNull Player player) {
        return player.isOnline() && receiverCollection.contains(player);
    }

    @Override
    public synchronized void addReceivers(@NonNull Player... players) {
        receiverCollection.addAll(Arrays.asList(players));

        addViewers(players);
    }

    @Override
    public synchronized void removeReceivers(@NonNull Player... players) {
        receiverCollection.removeAll(Arrays.asList(players));

        removeViewers(players);
    }

    @Override
    public synchronized boolean hasViewer(@NonNull Player player) {
        return player.isOnline() && viewerCollection.contains(player);
    }

    @Override
    public synchronized void addViewers(@NonNull Player... players) {
        FakeEntityRegistry.INSTANCE.registerEntity(this);

        for (Player player : players) {
            viewerCollection.add(player);

            sendSpawnPackets(player);
            sendDataWatcherPacket(player);

            entityEquipment.updateEquipmentPacket(player);
        }
    }

    @Override
    public synchronized void removeViewers(@NonNull Player... players) {
        for (Player player : players) {

            sendDestroyPackets(player);
            viewerCollection.remove(player);
        }
    }


    @Override
    public synchronized void sendSpawnPackets(@NonNull Player player) {
        for (AbstractPacket abstractPacket : getSpawnPackets())
            abstractPacket.sendPacket(player);
    }

    @Override
    public synchronized void sendDestroyPackets(@NonNull Player player) {
        for (AbstractPacket abstractPacket : getDestroyPackets())
            abstractPacket.sendPacket(player);
    }


    @Override
    public synchronized Collection<AbstractPacket> getSpawnPackets() {
        return Collections.singletonList(
                ProtocolPacketFactory.createSpawnEntityPacket(entityId, getSpawnTypeId(), 1, location));
    }

    @Override
    public synchronized Collection<AbstractPacket> getDestroyPackets() {
        return Collections.singletonList(
                ProtocolPacketFactory.createEntityDestroyPacket(entityId));
    }

    @Override
    public synchronized void teleport(@NonNull Location location) {
        this.location = location;

        viewerCollection.forEach(this::sendTeleportPacket);
    }

    @Override
    public synchronized void look(@NonNull Player player, @NonNull Location location) {
        Vector vector = location.clone().subtract(this.location).toVector().normalize();

        this.location.setDirection(vector);

        location.setYaw(this.location.getYaw());
        location.setPitch(this.location.getPitch());

        sendEntityLookPacket(player);
        sendHeadRotationPacket(player);
    }

    @Override
    public synchronized void look(@NonNull Player player) {
        look(player, player.getLocation());
    }

    @Override
    public synchronized void look(@NonNull Location location) {
        viewerCollection.forEach(player -> look(player, location));
    }

    @Override
    public synchronized void look(@NonNull Player player, float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);

        sendEntityLookPacket(player);
        sendHeadRotationPacket(player);
    }

    @Override
    public synchronized void look(float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);

        for (Player receiver : getViewerCollection()) {
            sendEntityLookPacket(receiver);
            sendHeadRotationPacket(receiver);
        }
    }

    @Override
    public synchronized void setPassengers(int... entityIds) {
        if (viewerCollection.isEmpty()) {
            return;
        }

        WrapperPlayServerMount mountPacket = new WrapperPlayServerMount();

        mountPacket.setEntityID(entityId);
        mountPacket.setPassengerIds(entityIds);

        viewerCollection.forEach(mountPacket::sendPacket);
    }

    @Override
    public synchronized void setPassengers(FakeEntity... fakeEntities) {
        int[] entityIds = Arrays.stream(fakeEntities).mapToInt(FakeEntity::getEntityId).toArray();

        setPassengers(entityIds);
    }

    @Override
    public synchronized void setVelocity(@NonNull Vector vector) {
        WrapperPlayServerEntityVelocity velocityPacket = new WrapperPlayServerEntityVelocity();
        velocityPacket.setEntityID(entityId);

        velocityPacket.setVelocityX(vector.getX());
        velocityPacket.setVelocityX(vector.getY());
        velocityPacket.setVelocityX(vector.getZ());

        viewerCollection.forEach(velocityPacket::sendPacket);
    }

    @Override
    public synchronized void setVelocity(@NonNull Player player, @NonNull Vector vector) {
        WrapperPlayServerEntityVelocity velocityPacket = new WrapperPlayServerEntityVelocity();
        velocityPacket.setEntityID(entityId);

        velocityPacket.setVelocityX(vector.getX());
        velocityPacket.setVelocityX(vector.getY());
        velocityPacket.setVelocityX(vector.getZ());

        velocityPacket.sendPacket(player);
    }

    protected synchronized void sendTeleportPacket(@NonNull Player player) {
        WrapperPlayServerEntityTeleport teleportPacket = new WrapperPlayServerEntityTeleport();

        teleportPacket.setEntityID(entityId);

        teleportPacket.setX(location.getX());
        teleportPacket.setY(location.getY());
        teleportPacket.setZ(location.getZ());

        teleportPacket.setYaw(location.getYaw());
        teleportPacket.setPitch(location.getPitch());

        teleportPacket.sendPacket(player);
    }

    protected synchronized void sendEntityLookPacket(@NonNull Player player) {
        WrapperPlayServerEntityLook entityLookPacket = new WrapperPlayServerEntityLook();

        entityLookPacket.setEntityID(entityId);
        entityLookPacket.setYaw(location.getYaw());
        entityLookPacket.setPitch(location.getPitch());

        entityLookPacket.sendPacket(player);
    }

    protected synchronized void sendHeadRotationPacket(@NonNull Player player) {
        WrapperPlayServerEntityHeadRotation headRotation = new WrapperPlayServerEntityHeadRotation();

        headRotation.setEntityID(entityId);
        headRotation.setHeadYaw((byte) (location.getYaw() * 256.0F / 360.0F));

        headRotation.sendPacket(player);
    }

    protected synchronized void sendDataWatcherPacket(@NonNull Player player) {
        if (!player.isOnline())
            return;

        ProtocolPacketFactory.createEntityMetadataPacket(entityId, dataWatcher)
                .sendPacket(player);
    }

    protected synchronized void broadcastDataWatcherPacket() {
        if (viewerCollection.isEmpty()) {
            return;
        }

        WrapperPlayServerEntityMetadata entityMetadataPacket
                = ProtocolPacketFactory.createEntityMetadataPacket(entityId, dataWatcher);

        viewerCollection.forEach(entityMetadataPacket::sendPacket);
    }

    protected synchronized void broadcastDataWatcherObject(int dataWatcherIndex,
                                                           @NonNull WrappedDataWatcher.Serializer serializer, @NonNull Object value) {

        WrappedDataWatcher.WrappedDataWatcherObject wrappedDataWatcherObject
                = new WrappedDataWatcher.WrappedDataWatcherObject(dataWatcherIndex, serializer);

        getDataWatcher().setObject(wrappedDataWatcherObject, value);
        broadcastDataWatcherPacket();
    }


    @Override
    public synchronized void setBurning(boolean burning) {
        if (this.burning == burning) {
            return;
        }

        this.burning = burning;
        broadcastDataWatcherObject(0, BYTE_SERIALIZER, generateBitMask());
    }

    @Override
    public synchronized void setSneaking(boolean sneaking) {
        if (this.sneaking == sneaking) {
            return;
        }

        this.sneaking = sneaking;
        broadcastDataWatcherObject(0, BYTE_SERIALIZER, generateBitMask());
    }

    @Override
    public void setSprinting(boolean sprinting) {
        if (this.sprinting == sprinting) {
            return;
        }

        this.sprinting = sprinting;
        broadcastDataWatcherObject(0, BYTE_SERIALIZER, generateBitMask());
    }

    @Override
    public synchronized void setInvisible(boolean invisible) {
        if (this.invisible == invisible) {
            return;
        }

        this.invisible = invisible;
        broadcastDataWatcherObject(0, BYTE_SERIALIZER, generateBitMask());
    }

    @Override
    public synchronized void setElytraFlying(boolean elytraFlying) {
        if (this.elytraFlying == elytraFlying) {
            return;
        }

        this.elytraFlying = elytraFlying;
        broadcastDataWatcherObject(0, BYTE_SERIALIZER, generateBitMask());
    }

    @Override
    public synchronized void setGlowingColor(@NonNull ChatColor glowingColor) {
        if (this.glowingColor == glowingColor) {
            return;
        }

        this.glowingColor = glowingColor;
        broadcastDataWatcherObject(0, BYTE_SERIALIZER, generateBitMask());
    }

    @Override
    public synchronized void setCustomNameVisible(boolean customNameVisible) {
        if (this.customNameVisible == customNameVisible) {
            return;
        }

        this.customNameVisible = customNameVisible;
        broadcastDataWatcherObject(3, BOOLEAN_SERIALIZER, customNameVisible);
    }

    @Override
    public synchronized void setNoGravity(boolean noGravity) {
        if (this.noGravity == noGravity) {
            return;
        }

        this.noGravity = noGravity;
        broadcastDataWatcherObject(5, BOOLEAN_SERIALIZER, noGravity);
    }

    @Override
    public synchronized void setCustomName(@NonNull String customName) {
        if (this.customName != null && this.customName.equals(customName)) {
            return;
        }

        this.customName = customName;
        broadcastDataWatcherObject(2, STRING_SERIALIZER, customName);
    }


    @Override
    public synchronized void setCustomName(@NonNull Player player, @NonNull String customName) {

        // update entity metadata objects
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();

        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, STRING_SERIALIZER), customName);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, BOOLEAN_SERIALIZER), true);


        // send entity metadata packet
        WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata();

        entityMetadataPacket.setEntityID(entityId);
        entityMetadataPacket.setMetadata(wrappedDataWatcher.getWatchableObjects());

        entityMetadataPacket.sendPacket(player);
    }

    @SneakyThrows
    public synchronized FakeBaseEntity clone() {
        return (FakeBaseEntity) super.clone();
    }

    private synchronized byte generateBitMask() {
        return (byte) ((burning ? 0x01 : 0) + (sneaking ? 0x02 : 0) + (sprinting ? 0x08 : 0) + (invisible ? 0x20 : 0) + (glowingColor != null ? 0x40 : 0) + (elytraFlying ? 0x80 : 0));
    }

}
