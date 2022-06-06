package net.plazmix.game.listener;

import lombok.NonNull;
import net.plazmix.event.PlayerDamageByPlayerEvent;
import net.plazmix.event.PlayerDamageEvent;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.setting.GameSetting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public final class GameSettingsListener extends GameListener {

    private final GamePluginService pluginService;

    public GameSettingsListener(@NonNull GamePlugin plugin) {
        super(plugin);

        this.pluginService = plugin.getService();
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.WEATHER_CHANGE.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.FOOD_CHANGE.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.BLOCK_BREAK.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.BLOCK_PLACE.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.CREATURE_SPAWN_CUSTOM.get(pluginService, boolean.class));

        } else {

            event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.CREATURE_SPAWN_GENERIC.get(pluginService, boolean.class));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.PLAYER_DROP_ITEM.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.PLAYER_PICKUP_ITEM.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(PlayerDamageEvent event) {

        if (GameSetting.ENTITY_DAMAGE.get(pluginService, boolean.class)) {
            event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.PLAYER_DAMAGE.get(pluginService, boolean.class));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(PlayerDamageByPlayerEvent event) {

        if (GameSetting.ENTITY_DAMAGE.get(pluginService, boolean.class)) {
            event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.PLAYER_DAMAGE.get(pluginService, boolean.class));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        boolean settingsAllow = pluginService.isSettingsChangeAllow();
        boolean entityDamage = GameSetting.ENTITY_DAMAGE.get(pluginService, boolean.class);

        event.setCancelled(settingsAllow && !entityDamage);

        if (entityDamage && event.getEntity() instanceof Player) {
            event.setCancelled(settingsAllow && !GameSetting.PLAYER_DAMAGE.get(pluginService, boolean.class));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        boolean settingsAllow = pluginService.isSettingsChangeAllow();
        boolean entityDamage = GameSetting.ENTITY_DAMAGE.get(pluginService, boolean.class);

        event.setCancelled(settingsAllow && !entityDamage);

        if (entityDamage && event.getEntity() instanceof Player) {
            event.setCancelled(settingsAllow && !GameSetting.PLAYER_DAMAGE.get(pluginService, boolean.class));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.ENTITY_EXPLODE.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.BLOCK_PHYSICS.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPhysics(BlockBurnEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.BLOCK_BURN.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.LEAVES_DECAY.get(pluginService, boolean.class));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractItem(PlayerInteractEvent event) {

        if (event.hasItem()) {
            event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.INTERACT_ITEM.get(pluginService, boolean.class));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractBlock(PlayerInteractEvent event) {

        if (event.hasBlock()) {
            event.setCancelled(pluginService.isSettingsChangeAllow() && !GameSetting.INTERACT_BLOCK.get(pluginService, boolean.class));
        }
    }

}
