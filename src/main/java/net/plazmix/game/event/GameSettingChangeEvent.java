package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomCancellableEvent;
import net.plazmix.game.setting.GameSetting;

@RequiredArgsConstructor
@Getter
public class GameSettingChangeEvent extends BaseCustomCancellableEvent {

    private final GameSetting gameSetting;
    private final Object previousValue, currentValue;
}
