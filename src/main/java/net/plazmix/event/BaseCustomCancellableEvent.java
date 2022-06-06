package net.plazmix.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Setter
@Getter
public abstract class BaseCustomCancellableEvent
        extends BaseCustomEvent
        implements Cancellable {

    private boolean cancelled;
}
