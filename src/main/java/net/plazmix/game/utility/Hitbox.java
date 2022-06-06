package net.plazmix.game.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Hitbox {

    HEAD("Голова", 1.5D, 2.0D),
    BODY("Тело", 0.6D, 1.5D),
    LEGS("Ноги",0.0D, 0.6D),

    UNKNOWN("N/A",-1, -1),
    ;

    String title;

    double minDistance;
    double maxDistance;

    public static Hitbox fromHitLocation(@NonNull Entity target, @NonNull Location hitLocation) {
        double distanceY = hitLocation.getY() - target.getLocation().getY();

        for (Hitbox hitbox : Hitbox.values()) {

            if (distanceY >= hitbox.minDistance && distanceY <= hitbox.maxDistance) {
                return hitbox;
            }
        }

        return UNKNOWN;
    }

}
