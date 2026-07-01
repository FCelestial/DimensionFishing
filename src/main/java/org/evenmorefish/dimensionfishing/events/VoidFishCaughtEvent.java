package org.evenmorefish.dimensionfishing.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class VoidFishCaughtEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final TrackedHook hook;

    @ApiStatus.Internal
    public VoidFishCaughtEvent(@NotNull TrackedHook hook) {
        this.hook = hook;
    }

    public @NotNull TrackedHook getTrackedHook() {
        return this.hook;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static boolean hasListeners() {
        if (handlers.getRegisteredListeners().length == 0) {
            DimensionFishing.getInstance().getLogger().warning("There are no listeners for VoidFishCaughtEvent.");
            return false;
        }
        return true;
    }

}
