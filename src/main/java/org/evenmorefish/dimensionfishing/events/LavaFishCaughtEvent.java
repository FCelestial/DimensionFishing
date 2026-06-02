package org.evenmorefish.dimensionfishing.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerFishEvent;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class LavaFishCaughtEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final PlayerFishEvent event;
    private final TrackedHook hook;

    @ApiStatus.Internal
    public LavaFishCaughtEvent(@NotNull PlayerFishEvent event, @NotNull TrackedHook hook) {
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) {
            throw new UnsupportedOperationException("Invalid PlayerFishEvent State.");
        }
        this.event = event;
        this.hook = hook;
    }

    public @NotNull PlayerFishEvent getOriginalEvent() {
        return this.event;
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

}
