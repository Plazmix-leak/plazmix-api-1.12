package net.plazmix.utility.cooldown;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class CooldownUtil {
    
    private final Map<String, Long> cooldownMap = new HashMap<>();

    /**
     * Создание и добавление задержки
     */
    public void putCooldown(String cooldownName, long mills) {
        Objects.requireNonNull(cooldownName, "Name of cooldown == null");

        cooldownMap.put(cooldownName, System.currentTimeMillis() + mills);
    }

    /**
     * Получение оставшегося времени у задержки
     */
    public long getCooldown(String cooldownName) {
        Objects.requireNonNull(cooldownName, "Name of cooldown == null");

        Long cooldown = cooldownMap.get(cooldownName);
        return cooldown == null ? 0 : cooldown - System.currentTimeMillis();
    }

    /**
     * Удаление задержки
     */
    public void removeCooldown(String cooldownName) {
        Objects.requireNonNull(cooldownName, "Name of cooldown == null");

        cooldownMap.remove(cooldownName);
    }

    /**
     * Возвращает boolean, говорящий о том, действует ли
     * еще эта задержка.
     */
    public boolean hasCooldown(String cooldownName) {
        Objects.requireNonNull(cooldownName, "Name of cooldown == null");

        return getCooldown(cooldownName) > 0;
    }
    
}
