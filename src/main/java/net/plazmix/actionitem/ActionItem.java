package net.plazmix.actionitem;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.JsonUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class ActionItem {

    public static final String ACTION_NBT_TAG = "ActionItem";

    public static boolean isActionItem(@NonNull ItemStack itemStack) {
        return ((NbtCompound) NbtFactory.fromItemTag(itemStack)).containsKey(ACTION_NBT_TAG);
    }

    public static ActionItem find(@NonNull ItemStack itemStack) {
        if (!ActionItem.isActionItem(itemStack))
            return null;

        String itemJson = ((NbtCompound) NbtFactory.fromItemTag(itemStack)).getString(ACTION_NBT_TAG);
        return JsonUtil.fromJson(itemJson, ActionItem.class);
    }

    public static ActionItem create(@NonNull ItemStack itemStack, Player... playersForGive) {
        if (ActionItem.isActionItem(itemStack))
            return ActionItem.find(itemStack);

        ItemStack nmsItem = ItemUtil.newBuilder(itemStack).toCraftItem().build().clone();

        for (Player player : playersForGive) {
            player.getInventory().addItem(nmsItem);
        }

        NbtCompound itemNbt = ((NbtCompound) NbtFactory.fromItemTag(nmsItem));
        ActionItem actionItem = new ActionItem(nmsItem);

        itemNbt.put(ACTION_NBT_TAG, JsonUtil.toJson(actionItem));

        NbtFactory.setItemTag(nmsItem, itemNbt);
        return actionItem;
    }


    private final ItemStack itemStack;


    private ActionItemHandler<PlayerDropItemEvent> dropHandler;

    private ActionItemHandler<PlayerPickupItemEvent> pickupHandler;

    private ActionItemHandler<EntityDamageByEntityEvent> attackHandler;

    private ActionItemHandler<PlayerInteractEvent> interactHandler;

    private ActionItemHandler<BlockPlaceEvent> placeHandler;

    private ActionItemHandler<BlockBreakEvent> breakHandler;

    private ActionItemHandler<PlayerChangedWorldEvent> worldChangedHandler;

    private ActionItemHandler<PlayerReadyArrowEvent> readyArrowHandler;


    public ActionItem setDropHandler(ActionItemHandler<PlayerDropItemEvent> dropHandler) {
        this.dropHandler = dropHandler;
        return this;
    }

    public ActionItem setPickupHandler(ActionItemHandler<PlayerPickupItemEvent> pickupHandler) {
        this.pickupHandler = pickupHandler;
        return this;
    }

    public ActionItem setAttackHandler(ActionItemHandler<EntityDamageByEntityEvent> attackHandler) {
        this.attackHandler = attackHandler;
        return this;
    }

    public ActionItem setInteractHandler(ActionItemHandler<PlayerInteractEvent> interactHandler) {
        this.interactHandler = interactHandler;
        return this;
    }

    public ActionItem setPlaceHandler(ActionItemHandler<BlockPlaceEvent> placeHandler) {
        this.placeHandler = placeHandler;
        return this;
    }

    public ActionItem setBreakHandler(ActionItemHandler<BlockBreakEvent> breakHandler) {
        this.breakHandler = breakHandler;
        return this;
    }

    public ActionItem setWorldChangedHandler(ActionItemHandler<PlayerChangedWorldEvent> worldChangedHandler) {
        this.worldChangedHandler = worldChangedHandler;
        return this;
    }

    public ActionItem setReadyArrowHandler(ActionItemHandler<PlayerReadyArrowEvent> readyArrowHandler) {
        this.readyArrowHandler = readyArrowHandler;
        return this;
    }
}
