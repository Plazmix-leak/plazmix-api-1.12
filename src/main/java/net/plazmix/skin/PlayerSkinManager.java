package net.plazmix.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.*;
import net.plazmix.coreconnector.direction.bukkit.BukkitConnectorPlugin;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.coreconnector.utility.mojang.MojangSkin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.LinkedList;

public final class PlayerSkinManager {

    public static final PlayerSkinManager INSTANCE = new PlayerSkinManager();

    @SneakyThrows
    public void setSkin(boolean saveHistory, @NonNull Player player, @NonNull MojangSkin mojangSkin) {
        setSkin(player, mojangSkin.getValue(), mojangSkin.getSignature());

        if (saveHistory) {
            saveSkinHistory(player, mojangSkin.getSkinName());
        }
    }

    @SneakyThrows
    public void setSkin(boolean saveHistory, @NonNull Player player, @NonNull String skinName) {
        MojangSkin mojangSkin = MojangApi.getMojangSkinOrDefault(skinName);

        if (mojangSkin == null) {
            return;
        }

        setSkin(player, mojangSkin.getValue(), mojangSkin.getSignature());

        if (saveHistory) {
            saveSkinHistory(player, mojangSkin.getSkinName());
        }
    }

    @SneakyThrows
    public void setSkin(@NonNull Player player, @NonNull String value, @NonNull String signature) {
        setTextures(player, value, signature);

        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer nmsPlayer = craftPlayer.getHandle();

        WorldServer worldServer = (WorldServer) nmsPlayer.getWorld();

        EnumGamemode gameMode = EnumGamemode.valueOf(player.getGameMode().name());

        Location location = player.getLocation();

        GameProfile profile = nmsPlayer.getProfile();

        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", value, signature));

        PacketPlayOutPlayerInfo packetPlayOutRemoveTab
                = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, nmsPlayer);

        PacketPlayOutPlayerInfo packetPlayOutAddTab
                = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nmsPlayer);

        PacketPlayOutRespawn packetPlayOutRespawn
                = new PacketPlayOutRespawn(worldServer.dimension, worldServer.getDifficulty(), worldServer.worldData.getType(), gameMode);

        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata
                = new PacketPlayOutEntityMetadata(craftPlayer.getEntityId(), nmsPlayer.getDataWatcher(), true);

        PacketPlayOutPosition packetPlayOutPosition
                = new PacketPlayOutPosition(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), new HashSet<>(), -1337);

        PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;
        playerConnection.sendPacket(packetPlayOutRemoveTab);
        playerConnection.sendPacket(packetPlayOutAddTab);
        playerConnection.sendPacket(packetPlayOutRespawn);
        playerConnection.sendPacket(packetPlayOutEntityMetadata);
        playerConnection.sendPacket(packetPlayOutPosition);

        PlayerInventory inventory = player.getInventory();

        player.setExp(player.getExp());
        player.setWalkSpeed(player.getWalkSpeed());
        player.updateInventory();

        inventory.setHeldItemSlot(inventory.getHeldItemSlot());

        craftPlayer.updateScaledHealth();

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.canSee(player) || target == player) {
                continue;
            }

            target.hidePlayer(player);
            target.showPlayer(player);
        }
    }

    public void setTextures(@NonNull Player player, @NonNull String value, @NonNull String signature) {
        BukkitConnectorPlugin.SKIN_SETTER.updateSkin(player, value, signature);

        GameProfile profile = ((CraftPlayer) player).getProfile();

        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", value, signature));
    }

    public void saveSkinHistory(@NonNull Player player, @NonNull String skinName) {
        NetworkModule.getInstance().getSkinsModule().saveSkinHistory(player.getName(), skinName);
    }

    public LinkedList<PlayerSkin> getSkinsHistory(@NonNull String playerName) {
        return NetworkModule.getInstance().getSkinsModule().getSkinsHistory(playerName);
    }

    public PlayerSkin getCurrentPlayerSkin(@NonNull String playerName) {
        return NetworkModule.getInstance().getSkinsModule().getCurrentPlayerSkin(playerName);
    }

    public boolean hasSkinHistory(@NonNull String playerName, @NonNull String skinName) {
        return NetworkModule.getInstance().getSkinsModule().hasSkinHistory(playerName, skinName);
    }

}
