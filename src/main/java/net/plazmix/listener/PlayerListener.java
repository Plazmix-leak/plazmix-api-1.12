package net.plazmix.listener;

import com.comphenix.protocol.utility.MinecraftReflection;
import lombok.NonNull;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.coreconnector.module.type.coloredprefix.PlayerPrefixColorChangeEvent;
import net.plazmix.coreconnector.module.type.coloredprefix.PlayerPrefixColorResetEvent;
import net.plazmix.coreconnector.utility.StringUtils;
import net.plazmix.event.EntityDamageByPlayerEvent;
import net.plazmix.event.PlayerDamageByEntityEvent;
import net.plazmix.event.PlayerDamageByPlayerEvent;
import net.plazmix.event.PlayerDamageEvent;
import net.plazmix.protocollib.team.ProtocolTeam;
import net.plazmix.utility.leveling.LevelSqlHandler;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;

public class PlayerListener implements Listener {

    private ProtocolTeam getPlayerTeam(@NonNull Player player) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);
        String teamName = ((plazmixUser.getGroup() != null ? plazmixUser.getGroup().getTagPriority() : "") + plazmixUser.getName());

        ProtocolTeam protocolTeam = ProtocolTeam.get(StringUtils.fixLength(16, teamName));
        protocolTeam.setPrefix(plazmixUser.getPrefix());

        if (!protocolTeam.hasAutoReceived()) {
            protocolTeam.addAutoReceived();
        }

        return protocolTeam;
    }

    @EventHandler
    public void onPlayerPrefixColorReset(PlayerPrefixColorResetEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerName());

        if (player != null) {
            getPlayerTeam(player).broadcast();
        }
    }

    @EventHandler
    public void onPlayerPrefixColorChange(PlayerPrefixColorChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerName());

        if (player != null) {
            getPlayerTeam(player).broadcast();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // entity collision
        player.setCollidable(false);
        player.spigot().setCollidesWithEntities(false);

        // Так надо :(
        event.setJoinMessage(null);

        // фикс обновления игрока данных на новых версиях
        Bukkit.getScheduler().runTaskLater(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), () -> {

            // Add team
            ProtocolTeam protocolTeam = getPlayerTeam(player);
            protocolTeam.addPlayerEntry(player);

            try {
                MinecraftReflection.getCraftPlayerClass().getMethod("updateScaledHealth")
                        .invoke(player);
            }

            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

        }, 2);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlazmixUser.of(player).getDatabasesValuesCacheTable().clear();

        // Player level injection
        LevelSqlHandler.INSTANCE.playerExperienceMap.remove(PlazmixUser.of(player).getPlayerId());

        // Remove team
        for (ProtocolTeam protocolTeam : ProtocolTeam.findEntryList(player)) {
            protocolTeam.removePlayerEntry(player);
        }

        // Так надо :(
        event.setQuitMessage(null);
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity target = event.getEntity();

        if (damager instanceof Player && target instanceof Player) {
            PlayerDamageByPlayerEvent playerDamageByPlayerEvent = new PlayerDamageByPlayerEvent((Player) damager, (Player) target, event.getCause(), event.getDamage());
            Bukkit.getPluginManager().callEvent(playerDamageByPlayerEvent);

            event.setCancelled( playerDamageByPlayerEvent.isCancelled() );
        }

        else if (damager instanceof Player) {
            EntityDamageByPlayerEvent entityDamageByPlayerEvent = new EntityDamageByPlayerEvent((Player) damager, target, event.getCause(), event.getDamage());
            Bukkit.getPluginManager().callEvent(entityDamageByPlayerEvent);

            event.setCancelled( entityDamageByPlayerEvent.isCancelled() );
        }

        else if (target instanceof Player) {
            PlayerDamageByEntityEvent playerDamageByEntityEvent = new PlayerDamageByEntityEvent(damager, (Player) target, event.getCause(), event.getDamage());
            Bukkit.getPluginManager().callEvent(playerDamageByEntityEvent);

            event.setCancelled( playerDamageByEntityEvent.isCancelled() );
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        EntityDamageEvent.DamageCause damageCause = event.getCause();

        if (entity instanceof Player) {
            PlayerDamageEvent playerDamageEvent = new PlayerDamageEvent(((Player) entity), damageCause, event.getDamage());
            Bukkit.getPluginManager().callEvent(playerDamageEvent);

            event.setCancelled(playerDamageEvent.isCancelled());
        }
    }

}
