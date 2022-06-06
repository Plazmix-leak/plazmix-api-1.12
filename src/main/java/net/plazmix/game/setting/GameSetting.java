package net.plazmix.game.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.game.GamePluginService;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GameSetting {

    FOOD_CHANGE(false),
    WEATHER_CHANGE(false),
    BLOCK_BREAK(false),
    BLOCK_PLACE(false),
    BLOCK_BURN(false),
    INTERACT_ITEM(true),
    INTERACT_BLOCK(true),
    PLAYER_DAMAGE(false),
    PLAYER_DROP_ITEM(true),
    PLAYER_PICKUP_ITEM(true),
    ENTITY_DAMAGE(false),
    ENTITY_EXPLODE(false),
    CREATURE_SPAWN_GENERIC(true),
    CREATURE_SPAWN_CUSTOM(true),
    BLOCK_PHYSICS(true),
    LEAVES_DECAY(false),
    ;

    private Object defaultValue;

    public static void setAll(@NonNull GamePluginService gameService, Object value) {
        for (GameSetting gameSetting : values()) {
            gameSetting.set(gameService, value);
        }
    }

    public static void resetDefaults(@NonNull GamePluginService gameService) {
        GameSetting.FOOD_CHANGE.set(gameService, false);
        GameSetting.WEATHER_CHANGE.set(gameService, false);
        GameSetting.BLOCK_BREAK.set(gameService, false);
        GameSetting.BLOCK_PLACE.set(gameService, false);
        GameSetting.BLOCK_BURN.set(gameService, false);
        GameSetting.INTERACT_ITEM.set(gameService, true);
        GameSetting.INTERACT_BLOCK.set(gameService, true);
        GameSetting.PLAYER_DAMAGE.set(gameService, false);
        GameSetting.PLAYER_DROP_ITEM.set(gameService, true);
        GameSetting.PLAYER_PICKUP_ITEM.set(gameService, true);
        GameSetting.ENTITY_DAMAGE.set(gameService, false);
        GameSetting.ENTITY_EXPLODE.set(gameService, false);
        GameSetting.CREATURE_SPAWN_GENERIC.set(gameService, true);
        GameSetting.CREATURE_SPAWN_CUSTOM.set(gameService, true);
        GameSetting.BLOCK_PHYSICS.set(gameService, true);
        GameSetting.LEAVES_DECAY.set(gameService, false);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(@NonNull GamePluginService gameService) {
        if (!gameService.hasSettingValue(this)) {
            return (V) defaultValue;
        }

        return gameService.getSetting(this);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(@NonNull GamePluginService gameService, @NonNull Class<V> valueType) {
        if (!gameService.hasSettingValue(this)) {
            return (V) defaultValue;
        }

        return gameService.getSetting(this);
    }

    public void set(@NonNull GamePluginService gameService, Object value) {
        gameService.setSetting(this, (value == null ? defaultValue : value));
    }

}
