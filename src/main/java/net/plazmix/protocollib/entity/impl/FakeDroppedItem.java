package net.plazmix.protocollib.entity.impl;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.protocollib.entity.FakeBaseEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Getter
public class FakeDroppedItem extends FakeBaseEntity {

    private ItemStack itemStack;

    public FakeDroppedItem(@NonNull ItemStack itemStack, @NonNull Location location) {
        super(EntityType.DROPPED_ITEM, location);

        setItemStack(itemStack);
    }

    public FakeDroppedItem(@NonNull Location location) {
        this(new ItemStack(Material.BARRIER), location);
    }

    public synchronized void setItemStack(@NonNull ItemStack itemStack) {
        this.itemStack = itemStack;

        broadcastDataWatcherObject(6, ITEMSTACK_SERIALIZER, itemStack);
    }

    @Override
    public synchronized int getSpawnTypeId() {
        return 2;
    }

}
