package net.plazmix.protocollib.entity.impl;

import com.comphenix.protocol.wrappers.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.protocollib.entity.FakeBaseEntityLiving;
import net.plazmix.protocollib.packet.entity.WrapperPlayServerNamedEntitySpawn;
import net.plazmix.protocollib.packet.entity.WrapperPlayServerPlayerInfo;
import net.plazmix.protocollib.packet.scoreboard.WrapperPlayServerScoreboardTeam;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.mojang.MojangSkin;
import net.plazmix.utility.mojang.MojangUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.UUID;

@Getter
public class FakePlayer extends FakeBaseEntityLiving {

    private final UUID uuid;
    private final String name;

    private MojangSkin mojangSkin;
    private WrappedGameProfile wrappedGameProfile;


    public FakePlayer(@NonNull MojangSkin skin, @NonNull Location location) {
        super(EntityType.PLAYER, location);

        this.name = String.format("§8NPC [%s]", NumberUtil.randomInt(0, 999_999));
        this.uuid = UUID.randomUUID();

        this.mojangSkin = skin;

        updateSkinPart(PlayerSkinPart.TOTAL);
    }

    public FakePlayer(@NonNull String skin, @NonNull Location location) {
        this(MojangUtil.getMojangSkin(skin), location);
    }

    public FakePlayer(@NonNull Location location) {
        this("Steve", location);
    }

    public synchronized void updateSkinPart(byte skinParts) {
        broadcastDataWatcherObject(13, BYTE_SERIALIZER, skinParts);
    }

    public synchronized void updateSkinPart(@NonNull PlayerSkinPart... playerSkinParts) {
        byte skinParts = 0x00;

        for (PlayerSkinPart playerSkinPart : playerSkinParts) {
            skinParts += playerSkinPart.mask;
        }

        updateSkinPart(skinParts);
    }

    public synchronized void setSkin(@NonNull Player player, @NonNull MojangSkin mojangSkin) {
        this.mojangSkin = mojangSkin;
        sendPlayerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, player);

        new BukkitRunnable() {

            @Override
            public void run() {
                sendPlayerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, player);
            }

        }.runTaskLater(PlazmixApiPlugin.getProvidingPlugin(PlazmixApiPlugin.class), 30);
    }

    public synchronized void setSkin(@NonNull MojangSkin mojangSkin) {
        this.mojangSkin = mojangSkin;

        for (Player receiver : Bukkit.getOnlinePlayers())
            sendPlayerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, receiver);

        new BukkitRunnable() {

            @Override
            public void run() {

                for (Player receiver : Bukkit.getOnlinePlayers())
                    sendPlayerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, receiver);
            }

        }.runTaskLater(PlazmixApiPlugin.getProvidingPlugin(PlazmixApiPlugin.class), 30);
    }

    public synchronized void setSkin(@NonNull String skinName) {
        setSkin(MojangUtil.getMojangSkin(skinName));
    }

    @Override
    public synchronized void sendSpawnPackets(Player player) {
        String teamName = getTeamName();

        sendPlayerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, player);
        sendTeamPacket(teamName, player, WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);

        WrapperPlayServerNamedEntitySpawn spawned = new WrapperPlayServerNamedEntitySpawn();

        spawned.setEntityID(getEntityId());
        spawned.setPosition(getLocation().toVector());
        spawned.setPlayerUUID(uuid);

        spawned.setPitch(getLocation().getPitch());
        spawned.setYaw(getLocation().getYaw());

        spawned.setMetadata(getDataWatcher());
        spawned.sendPacket(player);

        sendEntityLookPacket(player);
        sendHeadRotationPacket(player);

        sendTeamPacket(teamName, player, WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED);

        new BukkitRunnable() {

            @Override
            public void run() {
                sendPlayerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, player);
            }

        }.runTaskLater(PlazmixApiPlugin.getProvidingPlugin(PlazmixApiPlugin.class), 30);
    }

    @Override
    public synchronized void sendDestroyPackets(@NonNull Player player) {
        super.sendDestroyPackets(player);

        sendTeamPacket(getTeamName(), player, WrapperPlayServerScoreboardTeam.Mode.TEAM_REMOVED);
    }

    private synchronized void sendPlayerInfoPacket(EnumWrappers.PlayerInfoAction action, Player player) {
        WrapperPlayServerPlayerInfo playerInfoPacket = new WrapperPlayServerPlayerInfo();

        this.wrappedGameProfile = new WrappedGameProfile(uuid, name);

        if (mojangSkin != null) {
            wrappedGameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", mojangSkin.getValue(), mojangSkin.getSignature()));
        }

        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0,
                EnumWrappers.NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(name));

        playerInfoPacket.setAction(action);
        playerInfoPacket.setData(Collections.singletonList(playerInfoData));

        playerInfoPacket.sendPacket(player);
    }

    private synchronized void sendTeamPacket(String teamName, Player player, int mode) {
        WrapperPlayServerScoreboardTeam scoreboardTeam = new WrapperPlayServerScoreboardTeam();

        scoreboardTeam.setName(teamName);
        scoreboardTeam.setMode(mode);

        scoreboardTeam.setCollisionRule("never");
        scoreboardTeam.setNameTagVisibility("never");

        if (mode == WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED || mode == WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED) {
            scoreboardTeam.setDisplayName(teamName);
            scoreboardTeam.setPrefix(getGlowingColor() == null ? "§8" : getGlowingColor().toString());
            scoreboardTeam.setPackOptionData(0);
            scoreboardTeam.setColor(0);

        } else {
            scoreboardTeam.setPlayers(Collections.singletonList(name));
        }

        scoreboardTeam.sendPacket(player);
    }

    @Override
    public synchronized void setGlowingColor(ChatColor glowingColor) {
        FakePlayer.super.setGlowingColor(glowingColor);

        getViewerCollection().forEach(receiver ->
                sendTeamPacket(getTeamName(), receiver, WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED));
    }

    protected synchronized String getTeamName() {
        String teamName = (name + "_TEAM");

        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        return teamName;
    }

    @RequiredArgsConstructor
    public enum PlayerSkinPart {

        CAPE((byte) 0x01),

        JACKET((byte) 0x02),

        RIGHT_HAND((byte) 0x04),
        LEFT_HAND((byte) 0x08),

        RIGHT_LEG((byte) 0x10),
        LEFT_LEG((byte) 0x20),

        HAT((byte) 0x40),
        UNUSED((byte) 0x80),

        TOTAL(
                (byte) (CAPE.getMask() + JACKET.getMask() +

                        RIGHT_HAND.getMask() + LEFT_HAND.getMask() +
                        RIGHT_LEG.getMask() + LEFT_LEG.getMask() +

                        HAT.getMask()
                ));


        @Getter
        private final byte mask;
    }
}
