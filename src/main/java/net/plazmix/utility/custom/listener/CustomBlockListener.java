package net.plazmix.utility.custom.listener;

import net.plazmix.utility.custom.CustomBlock;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class CustomBlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();

        ItemStack handItem = player.getInventory().getItemInMainHand().clone();
        handItem.setAmount(1);

        CustomBlock customBlock = CustomBlock.valueOf(handItem);

        if (customBlock != null) {
            customBlock.onPlayerPlace(blockPlaceEvent);
            customBlock.placeBlock(blockPlaceEvent.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Block block = blockBreakEvent.getBlock();

        CustomBlock customBlock = CustomBlock.toCustomBlock(blockBreakEvent.getBlock());

        if (customBlock != null) {
            customBlock.onPlayerBreak(blockBreakEvent);

            if (!blockBreakEvent.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                customBlock.drop(block.getLocation());
            }

            blockBreakEvent.setExpToDrop(0);
            blockBreakEvent.setDropItems(false);
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        CustomBlock customBlock = CustomBlock.toCustomBlock(clickedBlock);

        if (customBlock != null) {
            customBlock.onPlayerInteract(player, event.getAction(), clickedBlock.getLocation(), event);

            event.setCancelled(true);
        }
    }

}
