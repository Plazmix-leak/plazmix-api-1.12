package net.plazmix.lobby.npc;

import lombok.NonNull;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.utility.mojang.MojangSkin;
import org.bukkit.Location;

public abstract class ServerPlayerNPC
        extends ServerNPC<FakePlayer> {

    public ServerPlayerNPC(@NonNull String playerSkin,
                           @NonNull Location location) {
        super(location);

        setHandle(new FakePlayer(playerSkin, location));
    }

    public ServerPlayerNPC(@NonNull MojangSkin mojangSkin,
                           @NonNull Location location) {
        super(location);

        setHandle(new FakePlayer(mojangSkin, location));
    }

    protected abstract void onReceive(@NonNull FakePlayer entity);
}
