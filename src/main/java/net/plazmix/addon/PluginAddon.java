package net.plazmix.addon;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
public abstract class PluginAddon {

    private static final Multimap<String, PluginAddon> pluginAddonsMap = HashMultimap.create();

    public static Collection<PluginAddon> getPluginAddons(@NonNull Plugin plugin) {
        return pluginAddonsMap.get(plugin.getName().toLowerCase());
    }


    private final String pluginName;
    private boolean handled;


    protected abstract void onEnable(@NonNull Plugin providerPlugin);
    protected abstract void onDisable(@NonNull Plugin providerPlugin);

    protected Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }

    protected void checkPlugin() {
        Preconditions.checkArgument(getPlugin() != null, "override plugin");
    }

    public void handleEnable(@NonNull Plugin providerPlugin) {
        Plugin plugin = getPlugin();

        if (!handled && plugin != null) {

            onEnable(providerPlugin);
            handled = true;
        }
    }

    public void handleDisable(@NonNull Plugin providerPlugin) {
        if (handled) {

            onDisable(providerPlugin);
            handled = false;
        }
    }

    public void register(@NonNull Plugin providerPlugin) {
        handleEnable(providerPlugin);

        pluginAddonsMap.put(pluginName.toLowerCase(), this);
    }
}
