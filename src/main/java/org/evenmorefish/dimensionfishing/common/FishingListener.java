package org.evenmorefish.dimensionfishing.common;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.scheduler.BukkitTask;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class FishingListener implements Listener {

    private static final FishingListener instance = new FishingListener();

    private final Map<UUID, FishHook> lavaHooks = new HashMap<>();
    private final Map<UUID, FishHook> voidHooks = new HashMap<>();

    private final BukkitTask task;

    private FishingListener() {
        this.task = Bukkit.getScheduler().runTaskTimer(
            DimensionFishing.getInstance(),
            () -> {
                checkHooks(lavaHooks, FishingState.LAVA, this::checkLava);
                checkHooks(voidHooks, FishingState.VOID, this::checkVoid);
            },
            1L,
            1L
        );
    }

    public static @NonNull FishingListener getInstance() {
        return instance;
    }

    public void shutdown() {
        task.cancel();
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        switch (event.getState()) {
            case FISHING -> {
                FishHook hook = event.getHook();
                World world = hook.getWorld();
                if (MainConfig.getInstance().getLavaAllowedWorlds().contains(world.getName())) {
                    lavaHooks.put(event.getPlayer().getUniqueId(), hook);
                }
                if (MainConfig.getInstance().getVoidAllowedWorlds().contains(world.getName())) {
                    voidHooks.put(event.getPlayer().getUniqueId(), hook);
                }
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
        if (hooked != null && hooked.getPersistentDataContainer().has(HookManager.STAND_KEY)) {
            hooked.remove();
        }
    }

    /**
     * Intended to be checked every tick inside a runnable.
     */
    private void checkHooks(@NonNull Map<UUID, FishHook> hooks, @NonNull FishingState state, @NonNull Function<FishHook, Boolean> func) {
        if (hooks.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, FishHook>> iterator = hooks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FishHook> entry = iterator.next();
            UUID uuid = entry.getKey();
            FishHook hook = entry.getValue();
            if (!hook.isValid()) {
                iterator.remove();
                continue;
            }
            if (func.apply(hook)) {
                HookManager.getInstance().startFishing(state, uuid, hook);
                iterator.remove();
            }
        }
    }

    private boolean checkLava(@NonNull FishHook hook) {
        System.out.println(hook.getLocation().getBlock().getType() == Material.LAVA ? "Was lava" : "Was not lava");
        return hook.getLocation().getBlock().getType() == Material.LAVA;
    }

    // TODO implement
    private boolean checkVoid(@NonNull FishHook hook) {
        if (!MainConfig.getInstance().getVoidAllowedWorlds().contains(hook.getWorld().getName())) {
            return false;
        }
        return false;
    }

}
