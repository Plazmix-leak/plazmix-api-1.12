package net.plazmix.actionitem;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public final class ActionItemListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getDropHandler() != null) {
                actionItem.getDropHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getPickupHandler() != null) {
                actionItem.getPickupHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        handleMainHand((Player) event.getDamager(), (player, actionItem) -> {

            if (actionItem.getAttackHandler() != null) {
                actionItem.getAttackHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }

        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getInteractHandler() != null) {
                actionItem.getInteractHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getPlaceHandler() != null) {
                actionItem.getPlaceHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getBreakHandler() != null) {
                actionItem.getBreakHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getWorldChangedHandler() != null) {
                actionItem.getWorldChangedHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void onReadyArrow(PlayerReadyArrowEvent event) {
        handleItem(event.getPlayer(), event.getArrow(), (player, actionItem) -> {

            if (actionItem.getReadyArrowHandler() != null) {
                actionItem.getReadyArrowHandler().handleEvent(event);
            }
        });
    }

    private void handleItem(@NonNull Player player, @NonNull ItemStack itemStack,
                            @NonNull BiConsumer<Player, ActionItem> itemConsumer) {

        if (ActionItem.isActionItem(itemStack)) {
            itemConsumer.accept(player, ActionItem.find(itemStack));
        }
    }

    private void handleMainHand(@NonNull Player player, @NonNull BiConsumer<Player, ActionItem> itemConsumer) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if (mainHandItem == null) {
            return;
        }

        if (ActionItem.isActionItem(mainHandItem)) {
            itemConsumer.accept(player, ActionItem.find(mainHandItem));
        }
    }

}
