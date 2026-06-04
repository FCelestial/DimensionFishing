package org.evenmorefish.dimensionfishing.common;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.evenmorefish.dimensionfishing.util.Keys;

public class FishingListener implements Listener {

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        switch (event.getState()) {
            case FISHING -> {
                TrackedHook hook = new TrackedHook(event.getPlayer(), event.getHook());
                if (!hook.shouldStartCustomTick()) {
                    return;
                }
                HookManager.getInstance().trackHook(hook);
            }
            // This is used instead of CAUGHT_FISH as the hook is attached to an armor stand.
            case CAUGHT_ENTITY -> HookManager.getInstance().checkCaughtEntity(event);
        }
    }

    // Cleans up any armor stands if the hook is removed from the world.
    @EventHandler
    public void onHookRemoved(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof FishHook hook)) {
            return;
        }
        Entity hooked = hook.getHookedEntity();
        if (hooked != null && hooked.getPersistentDataContainer().has(Keys.STAND_KEY)) {
            System.out.println("Removing armor stand because the hook was removed from the world.");
            hooked.remove();
        }
    }

}
