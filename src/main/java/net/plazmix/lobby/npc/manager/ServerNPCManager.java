package net.plazmix.lobby.npc.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.lobby.npc.ServerNPC;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ServerNPCManager {

    public static final ServerNPCManager INSTANCE = new ServerNPCManager();

    private final Collection<ServerNPC<?>> serverNpcsCollection = new ArrayList<>();

    public void register(@NonNull ServerNPC<?> serverNPC) {
        serverNpcsCollection.add(serverNPC);
    }

    public void unregister(@NonNull ServerNPC<?> serverNPC) {
        serverNpcsCollection.remove(serverNPC);
    }
}
