package net.plazmix.game.user;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.game.GameCache;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.event.GameGhostChangeEvent;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.game.team.GameTeam;
import net.plazmix.utility.BukkitPotionUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class GameUser {

    private static final GamePluginService cachedService = new GamePluginService();
    public static GamePluginService getService() {
        return GamePlugin.getInstance() != null ? GamePlugin.getInstance().getService() : cachedService;
    }

    public static GameUser from(int playerID) {
        return getService().getGameUser(playerID);
    }

    public static GameUser from(@NonNull String playerName) {
        return getService().getGameUser(playerName);
    }

    public static GameUser from(@NonNull Player bukkitHandle) {
        return getService().getGameUser(bukkitHandle);
    }


    private final int playerId;
    private boolean ghost;

    private final GameCache cache = new GameCache();

    @Setter
    private GameTeam currentTeam;


    public String getName() {
        return NetworkModule.getInstance().getPlayerName(playerId);
    }

    public boolean isAlive() {
        return !isGhost();
    }

    /**
     * Установить игровому пользователю
     * статус наблюдателя игры
     *
     * @param isGhost - разрешение на статус
     */
    public void setGhost(boolean isGhost) {
        Player player = Bukkit.getPlayer(getName());

        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().clear();

        player.setGameMode(GameMode.ADVENTURE);

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);

        if (isGhost) {
            player.setMaximumNoDamageTicks(Integer.MAX_VALUE);
            player.setNoDamageTicks(Integer.MAX_VALUE);

            player.sendTitle("§3§lВы наблюдаете за игрой", "Чтобы покинуть игру, введите - §e/leave");
            player.sendMessage("§d§lGame §8:: §fВы были перемещены в режим наблюдателя");

            player.addPotionEffect(BukkitPotionUtil.getInfinityPotion(PotionEffectType.INVISIBILITY));
            player.addPotionEffect(BukkitPotionUtil.getInfinityPotion(PotionEffectType.SPEED));

           if (currentTeam != null) {
               currentTeam.removePlayer(this);
           }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (GameUser.from(onlinePlayer).isGhost()) {

                    onlinePlayer.showPlayer(player);
                    player.showPlayer(onlinePlayer);

                    continue;
                }

                onlinePlayer.hidePlayer(player);
            }

            player.setAllowFlight(true);
            player.setFlying(true);

        } else {

            player.setMaximumNoDamageTicks(0);
            player.setNoDamageTicks(0);

            player.setGameMode(Bukkit.getServer().getDefaultGameMode());

            player.setFlying(false);
            player.setAllowFlight(false);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                if (GameUser.from(onlinePlayer).isAlive()) {
                    onlinePlayer.showPlayer(player);
                    player.showPlayer(onlinePlayer);
                }
            }
        }

        Bukkit.getPluginManager().callEvent(new GameGhostChangeEvent(this, isGhost));
        this.ghost = isGhost;
    }


    public List<GameItem> getSelectedItems(int categoryID) {
        return cache.getOrDefault("SItems" + categoryID, ArrayList::new);
    }

    public List<GameItem> getSelectedItems(@NonNull GameItemsCategory gameItemsCategory) {
        return getSelectedItems(gameItemsCategory.getId());
    }

    public GameItem getSelectedItem(@NonNull GameItemsCategory gameItemsCategory) {
        return getSelectedItems(gameItemsCategory.getId()).stream().findFirst().orElse(null);
    }

    public void setSelectedItem(@NonNull GameItem gameItem) {
        gameItem.getParameter().onDefaultSelect(this);
    }

    public void unselectItem(@NonNull GameItem gameItem) {
        gameItem.getParameter().onDefaultUnselect(this);
    }


    public List<GameItem> getGameItems(int categoryID) {
        return cache.getOrDefault("BItems" + categoryID, ArrayList::new);
    }

    public List<GameItem> getGameItems(@NonNull GameItemsCategory gameItemsCategory) {
        return getGameItems(gameItemsCategory.getId());
    }

    public boolean hasItem(@NonNull GameItem gameItem) {
        return gameItem.getPrice().getCount() <= 0 || getGameItems(gameItem.getItemCategory()).contains(gameItem);
    }

    public boolean isItemSelected(@NonNull GameItem gameItem) {
        return getSelectedItems(gameItem.getItemCategory()).contains(gameItem);
    }

    public void addItem(@NonNull GameItem gameItem) {

        // Player cache
        List<GameItem> gameItems = getGameItems(gameItem.getItemCategory());
        gameItems.add(gameItem);

        cache.set("BItems" + gameItem.getItemCategory().getId(), gameItems);

        // Database
        gameItem.getParameter().onDefaultPurchased(this);
    }

    public Player getBukkitHandle() {
        return Bukkit.getPlayer(getName());
    }

    public PlazmixUser getPlazmixHandle() {
        return PlazmixUser.of(playerId);
    }

}
