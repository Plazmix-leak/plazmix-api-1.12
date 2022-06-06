package net.plazmix.core;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.core.group.GroupManager;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.module.type.party.CoreParty;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.coreconnector.utility.mojang.MojangSkin;
import net.plazmix.coreconnector.utility.server.ServerMode;
import net.plazmix.coreconnector.utility.server.ServerSubModeType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Это еще не все, потом буду новое сюда добавлять
public interface PlazmixCoreApi {

    FriendApi FRIEND_API    = new FriendApi();
    GroupApi GROUP_API      = new GroupApi();
    SkinApi SKIN_API        = new SkinApi();
    PartyApi PARTY_API      = new PartyApi();

    static int getGlobalOnline() {
        return NetworkModule.getInstance().getGlobalOnline();
    }

    static int getServerOnline(@NonNull String... servers) {
        return NetworkModule.getInstance().getOnline(servers);
    }

    static int getOnlineByServersPrefixes(@NonNull String... serversPrefix) {
        return NetworkModule.getInstance().getOnlineByPrefixes(serversPrefix);
    }

    static int getConnectedServersCount(@NonNull String serversPrefix) {
        return NetworkModule.getInstance().getConnectedServersCount(serversPrefix);
    }

    static String getCurrentServerName() {
        return CoreConnector.getInstance().getServerName();
    }

    static Collection<String> getConnectedServers(@NonNull String serversPrefix) {
        return NetworkModule.getInstance().getConnectedServers(serversPrefix);
    }

    static void redirect(@NonNull Player player, @NonNull String server) {
        NetworkModule.getInstance().redirect(player.getName(), server);
    }

    static void redirectToLobby(@NonNull Player player) {
        NetworkModule.getInstance().redirectToBest(player.getName(), ServerMode.getMode(getCurrentServerName()), ServerSubModeType.GAME_LOBBY);
    }

    static void dispatchCommand(@NonNull Player player, @NonNull String coreCommand) {
        NetworkModule.getInstance().executeCommand(player.getName(), coreCommand);
    }

    static boolean isCurrentServerArena() {
        return ServerMode.getSubMode(getCurrentServerName()).getType() == ServerSubModeType.GAME_ARENA;
    }

    static boolean isCurrentServerLobby() {
        return ServerMode.getSubMode(getCurrentServerName()).getType() == ServerSubModeType.GAME_LOBBY;
    }

    static boolean isCurrentServerSurvival() {
        return ServerMode.getSubMode(getCurrentServerName()).getType() == ServerSubModeType.SURVIVAL;
    }

    static boolean isCurrentServerMain() {
        return ServerMode.getSubMode(getCurrentServerName()).getType() == ServerSubModeType.MAIN;
    }

    static boolean isServerConnected(@NonNull String server) {
        return NetworkModule.getInstance().isServerConnected(server);
    }

    static boolean isCoreConnected() {
        return CoreConnector.getInstance().isConnected();
    }


    /**
     * Апи для менеджмента с друзьями
     * игрока
     */
    final class FriendApi {
        // TODO: Нужно бы сделать это в коре для начала...
    }

    /**
     * Апи для менеджмента с друзьями
     * игрока
     */
    final class GroupApi {

        private Group getGroup(@NonNull String playerName) {
            return GroupManager.INSTANCE.getPlayerGroup(playerName);
        }


        public int getGroupLevel(@NonNull String playerName) {
            return getGroup(playerName).getLevel();
        }

        public boolean isDefault(@NonNull String playerName) {
            return getGroup(playerName).isDefault();
        }

        public boolean isDonate(@NonNull String playerName) {
            return getGroup(playerName).isDonate();
        }

        public boolean isUniversal(@NonNull String playerName) {
            return getGroup(playerName).isUniversal();
        }

        public boolean isStaff(@NonNull String playerName) {
            return getGroup(playerName).isStaff();
        }

        public boolean isAdmin(@NonNull String playerName) {
            return getGroup(playerName).isAdmin();
        }

        public String getGroupTagPriority(@NonNull String playerName) {
            return getGroup(playerName).getTagPriority();
        }

        public ChatColor getGroupColor(@NonNull String playerName) {
            return getGroup(playerName).getColor();
        }

        public String getGroupName(@NonNull String playerName) {
            return getGroup(playerName).getName();
        }

        public String getGroupColouredName(@NonNull String playerName) {
            return getGroup(playerName).getColouredName();
        }

        public String getGroupPrefix(@NonNull String playerName) {
            return getGroup(playerName).getPrefix();
        }

        public String getGroupSuffix(@NonNull String playerName) {
            return getGroup(playerName).getSuffix();
        }
    }

    /**
     * Апи для менеджмента со скинами
     * игрока и их историей
     */
    final class SkinApi {

        public String getSkinNick(@NonNull Player player) {
            return NetworkModule.getInstance().getSkinsModule().getCurrentPlayerSkin(player.getName()).getSkinName();
        }

        public MojangSkin getSkinObject(@NonNull Player player) {
            return NetworkModule.getInstance().getSkinsModule().getCurrentPlayerSkin(player.getName()).getSkinObject();
        }

        public List<String> getHistory(@NonNull Player player) {
            return NetworkModule.getInstance().getSkinsModule().getSkinsHistory(player.getName()).stream().map(PlayerSkin::getSkinName).collect(Collectors.toList());
        }
    }

    /**
     * Апи для менеджмента с компаниями
     * игроков
     */
    final class PartyApi {

        private CoreParty getParty(@NonNull Player player) {
            return NetworkModule.getInstance().getPartyModule().getParty(player.getName());
        }

        public boolean hasParty(@NonNull Player player) {
            return getParty(player) != null;
        }

        public String getLeader(@NonNull Player player) {
            if (!hasParty(player)) {
                return null;
            }

            return getParty(player).getLeader();
        }

        public List<String> getMembers(@NonNull Player player) {
            if (!hasParty(player)) {
                return null;
            }

            return getParty(player).getMembers();
        }

        public boolean isLeader(@NonNull Player player, @NonNull String leader) {
            if (!hasParty(player)) {
                return false;
            }

            return getParty(player).isLeader(leader);
        }

        public boolean hasMember(@NonNull Player player, @NonNull String member) {
            if (!hasParty(player)) {
                return false;
            }

            return getParty(player).isMember(member);
        }
    }
}
