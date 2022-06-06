package net.plazmix.game;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GameCache {

    @Getter
    private final Map<String, Object> map = new HashMap<>();

    public void set(@NonNull String id, Object value) {
        map.put(id, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull String id) {
        return (T) map.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull String id, Class<T> valueType) {
        return (T) map.get(id);
    }

    public <T> T getOrDefault(@NonNull String id, Supplier<T> def) {
        if (!map.containsKey(id)) {

            if (def == null) {
                return null;
            }

            return def.get();
        }

        return get(id);
    }

    @SuppressWarnings("all")
    public <T> List<T> getList(@NonNull String id, @NonNull Class<T> generic) {
        return get(id, List.class);
    }

    public List<String> getStringList(@NonNull String id) {
        return getList(id, String.class);
    }

    public Location getLocation(@NonNull String id) {
        return get(id, Location.class);
    }

    public String getString(@NonNull String id) {
        return get(id, String.class);
    }

    public int getInt(@NonNull String id) {
        return getOrDefault(id, () -> 0);
    }

    public double getDouble(@NonNull String id) {
        return getOrDefault(id, () -> 0);
    }

    public long getLong(@NonNull String id) {
        return getOrDefault(id, () -> 0);
    }

    public <T> T compute(@NonNull String id, Supplier<T> def) {
        if (!map.containsKey(id.toLowerCase())) {

            if (def == null) {
                return null;
            }

            T defValue = def.get();
            set(id, defValue);

            return defValue;
        }

        return get(id);
    }

    public void add(@NonNull String id, int valueToAdd) {
        set(id, getOrDefault(id, () -> 0) + valueToAdd);
    }

    public void take(@NonNull String id, int valueToRemove) {
        set(id, getOrDefault(id, () -> 0) - valueToRemove);
    }


    public void increment(@NonNull String id) {
        add(id, 1);
    }

    public void decrement(@NonNull String id) {
        take(id, 1);
    }

    public void clear() {
        map.clear();
    }

    public boolean contains(@NonNull String id) {
        return map.containsKey(id);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

}
