package net.plazmix.holographic.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.holographic.line.LangHolographicLine;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProtocolHolographicManager {

    public static final ProtocolHolographicManager INSTANCE = new ProtocolHolographicManager();

    protected final Multimap<Player, ProtocolHolographic> playerHolographics = HashMultimap.create();


    /**
     * Получить список голограм, которые были показаны
     * для указанного игрока
     *
     * @param player - игрок
     */
    public Collection<ProtocolHolographic> getProtocolHolographics(Player player) {
        return playerHolographics.get(player);
    }

    /**
     * Кешировать голограмму для указанного игрока
     *
     * @param player - игрок
     * @param protocolHolographic - голограмма
     */
    public void addProtocolHolographic(Player player, ProtocolHolographic protocolHolographic) {
        playerHolographics.put(player, protocolHolographic);
    }

    public void runLocalizedLinesTaskUpdate(@NonNull Plugin plugin) {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (ProtocolHolographic holographic : new ArrayList<>(playerHolographics.values())) {
                    for (ProtocolHolographicLine line : holographic.getHolographicLines()) {

                        if (line instanceof LangHolographicLine) {
                            line.update();
                        }
                    }
                }
            }

        }.runTaskTimerAsynchronously(plugin, 20, 20);
    }

}
