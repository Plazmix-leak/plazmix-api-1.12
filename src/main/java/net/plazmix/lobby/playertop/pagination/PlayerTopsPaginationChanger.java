package net.plazmix.lobby.playertop.pagination;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.lobby.playertop.PlayerTopsHolographic;
import net.plazmix.lobby.playertop.PlayerTopsStorage;
import net.plazmix.protocollib.entity.animation.FakeEntityAnimation;
import net.plazmix.protocollib.packet.ProtocolPacketFactory;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerTopsPaginationChanger {

    public static PlayerTopsPaginationChanger create() {
        return new PlayerTopsPaginationChanger();
    }


    private final LinkedList<PlayerTopsStorage.PlayerTopsBuilder> playerTopsList
            = new LinkedList<>();

    public void addPlayerTops(PlayerTopsStorage.PlayerTopsBuilder playerTops) {
        playerTopsList.add(playerTops);
    }

    public void spawn() {
        if (playerTopsList.isEmpty()) {
            return;
        }

        LinkedList<PlayerTopsStorage> topsStorageList = getTopsStorageList();

        PlayerTopsStorage firstStorage = topsStorageList.getFirst();
        PlayerTopsStorage lastStorage = topsStorageList.getLast();

        if (firstStorage.equals(lastStorage)) {
            spawnPublic(firstStorage);
            return;
        }

        int currentPageIndex = 0;
        for (PlayerTopsStorage currentTopStorage : topsStorageList) {

            PlayerTopsHolographic holographic = currentTopStorage.getHolographic();
            if (holographic == null) continue;

            ProtocolHolographic protocolHolographic = holographic.getProtocolHolographic();
            if (protocolHolographic == null) continue;


            int nextPageIndex = (currentPageIndex + 1) >= topsStorageList.size() ? 0 : currentPageIndex + 1;
            PlayerTopsStorage nextTopStorage = topsStorageList.get(nextPageIndex);

            // Выставляем им новый клик
            for (ProtocolHolographicLine holographicLine : protocolHolographic.getHolographicLines()) {

                if (holographicLine.getFakeArmorStand() == null) {
                    continue;
                }

                Consumer<Player> holoAction = player ->
                {
                    // Hide old storage
                    hide(currentTopStorage, player);

                    // Show new storage
                    show(nextTopStorage, player);


                    // Play effect & sounds
                    player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 2, 2);
                    player.playEffect(getMiddleLineLocation(protocolHolographic), Effect.EXPLOSION_LARGE, 5);


                    // Play player hand animation
                    ProtocolPacketFactory.createAnimationPacket(player.getEntityId(), FakeEntityAnimation.SWING_MAIN_HAND.ordinal())
                            .broadcastPacket();
                };

                holographicLine.getFakeArmorStand().setClickAction(holoAction);
                holographicLine.getFakeArmorStand().setAttackAction(holoAction);
            }

            protocolHolographic.addEmptyHolographicLine();
            protocolHolographic.addOriginalHolographicLine("§e§o► §nНажмите, чтобы сменить страницу");
            protocolHolographic.addOriginalHolographicLine("§7Следующее: §f" + nextTopStorage.getHolographic().getStatsName());

            currentPageIndex++;
        }

        spawnPublic(firstStorage);
    }

    private LinkedList<PlayerTopsStorage> getTopsStorageList() {
        return playerTopsList.stream().map(playerTopsBuilder -> playerTopsBuilder.build(false))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private void spawnPublic(PlayerTopsStorage playerTopsStorage) {
        Plugin plugin = PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class);

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> show(playerTopsStorage, event.getPlayer()), 20);
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {

                for (PlayerTopsStorage playerTopsStorage : getTopsStorageList()) {
                    hide(playerTopsStorage, event.getPlayer());
                }
            }

        }, plugin);
    }

    private void show(PlayerTopsStorage playerTopsStorage, Player player) {
        if (playerTopsStorage.getSkull() != null) {
            playerTopsStorage.getSkull().getFakeGiantZombie().addReceivers(player);
        }

        if (playerTopsStorage.getHolographic() != null) {
            playerTopsStorage.getHolographic().getProtocolHolographic().addReceivers(player);
        }
    }

    private void hide(PlayerTopsStorage playerTopsStorage, Player player) {
        if (playerTopsStorage.getSkull() != null) {
            playerTopsStorage.getSkull().getFakeGiantZombie().removeReceivers(player);
        }

        if (playerTopsStorage.getHolographic() != null) {
            playerTopsStorage.getHolographic().getProtocolHolographic().removeReceivers(player);
        }
    }

    private Location getMiddleLineLocation(ProtocolHolographic protocolHolographic) {
        if (protocolHolographic.getHolographicLines().size() <= 2) {
            return null;
        }

        ProtocolHolographicLine firstLine = protocolHolographic.getHolographicLine(0);
        ProtocolHolographicLine lastLine = protocolHolographic.getHolographicLine(protocolHolographic.getHolographicLines().size() - 1);

        Location holographicLocation = protocolHolographic.getLocation();
        double y = lastLine.getLocation().getY() + ((firstLine.getLocation().getY() - lastLine.getLocation().getY()) / 2);

        return new Location(holographicLocation.getWorld(), holographicLocation.getX(), y + 0.5, holographicLocation.getZ());
    }

}
