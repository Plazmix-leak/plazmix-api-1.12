package net.plazmix.protocollib.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.NonNull;
import net.plazmix.protocollib.entity.equipment.FakeEntityEquipment;
import net.plazmix.protocollib.packet.AbstractPacket;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.function.Consumer;

public interface FakeEntity extends FakeEntityClickable {

    int getEntityId();

    int getSpawnTypeId();


    EntityType getEntityType();

    FakeEntityScope getEntityScope();

    String getCustomName();


    Location getLocation();

    World getWorld();

    Chunk getChunk();


    Consumer<Player> getAttackAction();

    Consumer<Player> getClickAction();


    ChatColor getGlowingColor();


    FakeEntityEquipment getEntityEquipment();

    WrappedDataWatcher getDataWatcher();


    Collection<Player> getViewerCollection();

    Collection<Player> getReceiverCollection();


    boolean isBurning();

    boolean isSneaking();

    boolean isSprinting();

    boolean isInvisible();

    boolean isElytraFlying();

    boolean isCustomNameVisible();

    boolean isNoGravity();


    void spawn();

    void spawn(boolean isPublic);

    void remove();


    void setAttackAction(Consumer<Player> attackAction);

    void setClickAction(Consumer<Player> clickAction);


    void setEntityScope(@NonNull FakeEntityScope entityScope);


    boolean hasReceiver(@NonNull Player player);

    void addReceivers(@NonNull Player... players);

    void removeReceivers(@NonNull Player... players);


    boolean hasViewer(@NonNull Player player);

    void addViewers(@NonNull Player... players);

    void removeViewers(@NonNull Player... players);


    void sendSpawnPackets(@NonNull Player player);

    void sendDestroyPackets(@NonNull Player player);


    Collection<AbstractPacket> getSpawnPackets();

    Collection<AbstractPacket> getDestroyPackets();


    void teleport(@NonNull Location location);


    void look(@NonNull Player player, @NonNull Location location);

    void look(@NonNull Player player);

    void look(@NonNull Location location);

    void look(@NonNull Player player, float yaw, float pitch);

    void look(float yaw, float pitch);


    void setPassengers(int... entityIds);

    void setPassengers(FakeEntity... fakeEntities);


    void setVelocity(@NonNull Vector vector);

    void setVelocity(@NonNull Player player, @NonNull Vector vector);


    void setGlowingColor(@NonNull ChatColor glowingColor);

    void setCustomName(@NonNull String customName);

    void setCustomName(@NonNull Player player, @NonNull String customName);


    void setBurning(boolean burning);

    void setSneaking(boolean sneaking);

    void setSprinting(boolean sprinting);

    void setInvisible(boolean invisible);

    void setElytraFlying(boolean elytraFlying);

    void setCustomNameVisible(boolean customNameVisible);

    void setNoGravity(boolean noGravity);
}
