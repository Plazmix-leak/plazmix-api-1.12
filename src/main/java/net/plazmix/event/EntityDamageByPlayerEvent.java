package net.plazmix.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

@AllArgsConstructor
@Getter
public class EntityDamageByPlayerEvent extends BaseCustomEvent {

    private final Player damager;
    private final Entity target;

    private final EntityDamageEvent.DamageCause damageCause;

    @Setter
    private double damage;
}
