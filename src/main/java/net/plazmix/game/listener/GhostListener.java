package net.plazmix.game.listener;

import lombok.NonNull;
import net.plazmix.event.PlayerDamageEvent;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.event.GameGhostChangeEvent;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.protocollib.team.ProtocolTeam;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GhostListener extends GameListener {

    public GhostListener(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodChange(FoodLevelChangeEvent event) {

        if (GameUser.from(event.getEntity().getName()).isGhost()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamageSelf(PlayerDamageEvent event) {

        if (GameUser.from(event.getPlayer()).isGhost()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamageTarget(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (GameUser.from(event.getDamager().getName()).isGhost()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.hasBlock() && GameUser.from(event.getPlayer()).isGhost()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {

        if (GameUser.from(event.getPlayer()).isGhost()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(PlayerPickupItemEvent event) {

        if (GameUser.from(event.getPlayer()).isGhost()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        if (!(target instanceof Player)) {
            return;
        }

        if (GameUser.from(player).isGhost()) {
            new PlayerInventoryCheckerMenu((Player) target).openInventory(player);

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGhost(GameGhostChangeEvent event) {
        Player player = event.getGameUser().getBukkitHandle();

        if (event.isGhost()) {
            ProtocolTeam protocolTeam = ProtocolTeam.get("A" + player.getName());

            // Remove all teams.
            for (ProtocolTeam other : ProtocolTeam.findEntryList(player)) {
                if (other.equals(protocolTeam)) {
                    continue;
                }

                ProtocolTeam.remove(other.getName());
            }

            // Update current team.
            protocolTeam.setPrefix("§7[⚔] " + ChatColor.stripColor(PlazmixUser.of(player).getPrefix()));

            protocolTeam.addPlayerEntry(player);
            protocolTeam.addAutoReceived();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (GameUser.from(player).isGhost()) {
            String chatFormat = ("§7[⚔] §8:: ") + PlayerUtil.getDisplayName(player) + " §8➥ " + PlayerUtil.getGroup(player).getSuffix() + event.getMessage();

            for (GameUser ghostUser : plugin.getService().getGhostPlayers()) {
                ghostUser.getBukkitHandle().sendMessage(chatFormat);
            }

            event.setCancelled(true);
        }
    }

    private static class PlayerInventoryCheckerMenu extends BaseSimpleInventory {
        private final PlayerInventory targetInventory;

        public PlayerInventoryCheckerMenu(@NonNull Player target) {
            super(target.getName(), 6);

            this.targetInventory = target.getInventory();
        }

        @Override
        public void drawInventory(Player player) {

            // Разделяем инвентарь на броню и предметы в инвентаре
            ItemStack redSplitterItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(14)
                    .setName(ChatColor.RESET.toString())
                    .build();

            for (int i = 1; i <= 9; i++) {
                setOriginalItem(9 + i, redSplitterItem);
            }

            // Выставляем слоты брони
            int armorSlotCounter = 0;

            for (ItemStack armorItem : targetInventory.getArmorContents()) {
                armorSlotCounter++;

                if (armorItem == null) {
                    continue;
                }

                setOriginalItem(armorSlotCounter, armorItem);
            }

            // Выставляем предметы из хранилища инвентаря
            int storageSlotCounter = 18;

            for (ItemStack storageItem : targetInventory.getContents()) {
                storageSlotCounter++;

                if (storageItem == null) {
                    continue;
                }

                setOriginalItem(storageSlotCounter, storageItem);
            }
        }
    }

}
