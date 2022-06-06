package net.plazmix.game.listener;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.game.GamePlugin;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
@Getter
public abstract class GameListener implements Listener {

    protected final @NonNull GamePlugin plugin;
}
