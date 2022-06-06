package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomEvent;
import net.plazmix.game.state.GameState;

@RequiredArgsConstructor
@Getter
public class GameStateSwitchEvent extends BaseCustomEvent {

    private final GameState previousState;
    private final GameState currentState;
}
