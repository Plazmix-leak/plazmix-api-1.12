package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomEvent;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.user.GameUser;

@RequiredArgsConstructor
@Getter
public class GameItemApplyEvent extends BaseCustomEvent {

    private final GameUser gameUser;
    private final GameItem gameItem;
}
