package net.plazmix.addon;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public final class PluginAddonListener implements Listener {

    private final Plugin providerPlugin;

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();

        for (PluginAddon pluginAddon : PluginAddon.getPluginAddons(plugin)) {
            pluginAddon.handleEnable(providerPlugin);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();

        for (PluginAddon pluginAddon : PluginAddon.getPluginAddons(plugin)) {
            pluginAddon.handleDisable(providerPlugin);
        }
    }

}
