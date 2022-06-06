package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomEvent;
import net.plazmix.game.user.GameUser;

@RequiredArgsConstructor
@Getter
public class GameGhostChangeEvent extends BaseCustomEvent {

    private final GameUser gameUser;

    private final boolean ghost;
}
