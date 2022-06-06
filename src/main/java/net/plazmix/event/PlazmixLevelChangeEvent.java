package net.plazmix.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public class PlazmixLevelChangeEvent extends BaseCustomEvent {

    private final Player player;
    private final int oldLevel, newLevel;
}
