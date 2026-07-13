package org.evenmorefish.dimensionfishing.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class LavaFishCaughtEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final TrackedHook hook;
    private boolean cancel;

    @ApiStatus.Internal
    public LavaFishCaughtEvent(@NotNull TrackedHook hook) {
        super(hook.getPlayer());
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
            DimensionFishing.getInstance().getLogger().warning("There are no listeners for LavaFishCaughtEvent.");
            return false;
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
