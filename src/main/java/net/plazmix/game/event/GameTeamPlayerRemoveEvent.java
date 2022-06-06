package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomCancellableEvent;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;

@RequiredArgsConstructor
@Getter
public class GameTeamPlayerRemoveEvent extends BaseCustomCancellableEvent {

    private final GameUser gameUser;
    private final GameTeam gameTeam;
}
