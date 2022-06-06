package net.plazmix.actionitem;

import org.bukkit.event.Event;

public interface ActionItemHandler<E extends Event> {

    void handleEvent(E event);
}
