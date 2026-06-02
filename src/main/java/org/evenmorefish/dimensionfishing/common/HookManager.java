package org.evenmorefish.dimensionfishing.common;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.events.LavaFishCaughtEvent;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HookManager {

    public static final NamespacedKey STAND_KEY = new NamespacedKey(DimensionFishing.getInstance(), "stand");

    private static final HookManager instance = new HookManager();
    private BukkitTask task;
    private final Map<UUID, TrackedHook> trackedHooks = new HashMap<>();

    private HookManager() {}

    public static @NonNull HookManager getInstance() {
        return instance;
    }

    public void load() {
        long interval = TimeUnit.SECONDS.toMillis(1);
        this.task = Bukkit.getScheduler().runTaskTimer(
            DimensionFishing.getInstance(),
            () -> { /* TODO write task code. */ },
            interval,
            interval
        );
    }

    public void shutdown() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void startFishing(@NonNull FishingState state, @NonNull UUID uuid, @NonNull FishHook hook) {
        if (!hook.isValid()) {
            System.out.println("Invalid hook");
            return;
        }
        System.out.println("Valid hook");
        // If a hook is already tracked, assume it is bugged and invalidate.
        if (trackedHooks.containsKey(uuid)) {
            System.out.println("Invalidating old hook");
            trackedHooks.remove(uuid).invalidate();
        }
        System.out.println("Spawning armor stand");
        ArmorStand stand = createNewStand(hook.getLocation());
        hook.setHookedEntity(stand);
        stand.addPassenger(hook);
        hook.setVisualFire(false);
        hook.setFireTicks(0);
        TrackedHook tracked = new TrackedHook(state, hook, stand);
        trackedHooks.put(uuid, tracked);
        Bukkit.broadcast(Component.text("Hook is now attached to armor stand."));
    }

    public void checkCaughtEntity(@NonNull PlayerFishEvent event) {
        Entity entity = event.getCaught();
        if (entity == null) {
            return;
        }
        if (entity.getPersistentDataContainer().has(STAND_KEY)) {
            TrackedHook tracked = trackedHooks.get(event.getPlayer().getUniqueId());
            if (tracked == null) {
                System.out.println("No entity tracked?");
                return;
            }
            trackedHooks.remove(event.getPlayer().getUniqueId());

            switch (tracked.state()) {
                case LAVA -> new LavaFishCaughtEvent(event, tracked).callEvent();
                case VOID -> {}
            }

            tracked.invalidate();
            event.getPlayer().sendPlainMessage("Reeled in armor stand");
            event.setCancelled(true);
        }
    }

    private ArmorStand createNewStand(@NonNull Location location) {
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setVisualFire(false);
        stand.setPersistent(false);
        stand.setGravity(false);
        stand.getPersistentDataContainer().set(
            STAND_KEY,
            PersistentDataType.BOOLEAN,
            true
        );

        // Set a small scale so the hook doesn't float.
        AttributeInstance scale = stand.getAttribute(Attribute.GENERIC_SCALE);
        if (scale != null) {
            scale.setBaseValue(0.1);
        }

        // For debug only. Remove when published.
        stand.setGlowing(true);
        return stand;
    }

}
