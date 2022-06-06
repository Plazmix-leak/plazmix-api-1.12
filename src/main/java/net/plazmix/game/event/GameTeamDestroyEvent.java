package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomEvent;
import net.plazmix.game.team.GameTeam;

@RequiredArgsConstructor
@Getter
public class GameTeamDestroyEvent extends BaseCustomEvent {

    private final GameTeam gameTeam;
}
