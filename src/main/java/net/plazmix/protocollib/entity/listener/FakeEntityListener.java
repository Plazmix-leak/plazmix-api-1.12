package net.plazmix.protocollib.entity.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.protocollib.entity.FakeEntity;
import net.plazmix.protocollib.entity.FakeEntityLiving;
import net.plazmix.protocollib.entity.FakeEntityRegistry;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.function.Consumer;

public class FakeEntityListener extends PacketAdapter
        implements Listener {

    private static final long ENTITY_INTERACT_COOLDOWN = 50;

    public FakeEntityListener() {
        super(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class),
                PacketType.Play.Client.USE_ENTITY, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.UNLOAD_CHUNK);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketType type = event.getPacketType();
        Player player = event.getPlayer();

        StructureModifier<Integer> integers = event.getPacket().getIntegers();

        int x = integers.read(0);
        int z = integers.read(1);

        if (type == PacketType.Play.Server.UNLOAD_CHUNK) {
            onChunkUnload(player, x, z);
            return;
        }

        onChunkLoad(player, x, z);
    }

    private void onChunkLoad(Player player, int x, int z) {
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {

            for (FakeEntity entity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {
                Chunk chunk = entity.getLocation().getChunk();

                if (chunk.getX() == x && chunk.getZ() == z) {
                    if (entity.hasViewer(player)) {
                        continue;
                    }

                    entity.addViewers(player);
                }
            }

        }, 10L);
    }

    private void onChunkUnload(Player player, int x, int z) {
        for (FakeEntity entity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {
            Chunk chunk = entity.getLocation().getChunk();

            if (chunk.getX() == x && chunk.getZ() == z) {
                if (!entity.hasViewer(player)) {
                    continue;
                }

                entity.removeViewers(player);
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();

        if (PlayerCooldownUtil.hasCooldown("fake_entity_interact", player)) {
            return;
        }

        FakeEntity fakeEntity = FakeEntityRegistry.INSTANCE.getEntityById(event.getPacket().getIntegers().read(0));
        if (!(fakeEntity instanceof FakeEntityLiving)) {
            return;
        }

        EnumWrappers.EntityUseAction entityUseAction = event.getPacket().getEntityUseActions().read(0);
        switch (entityUseAction) {

            case ATTACK: {
                Consumer<Player> attackAction = fakeEntity.getAttackAction();

                if (attackAction != null) {
                    Bukkit.getScheduler().runTask(getPlugin(), () -> attackAction.accept(player));
                }

                break;
            }

            case INTERACT_AT: {
                Consumer<Player> clickAction = fakeEntity.getClickAction();

                if (clickAction != null) {
                    Bukkit.getScheduler().runTask(getPlugin(), () -> clickAction.accept(player));
                }

                break;
            }
        }

        PlayerCooldownUtil.putCooldown("fake_entity_interact", player, ENTITY_INTERACT_COOLDOWN);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (FakeEntity fakeEntity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {
            if (fakeEntity == null) {
                continue;
            }

            fakeEntity.removeReceivers(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {

            for (FakeEntity fakeEntity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {

                boolean equalsWorld = fakeEntity.getLocation().getWorld().equals(player.getWorld());
                boolean hasReceiver = fakeEntity.hasReceiver(player);

                if (!hasReceiver && equalsWorld) {
                    fakeEntity.addReceivers(player);
                }

                if (hasReceiver && !equalsWorld) {
                    fakeEntity.removeReceivers(player);
                }
            }
        }, 10);
    }

}