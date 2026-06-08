package org.evenmorefish.dimensionfishing.common;

import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.LureTracker;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.enums.CatchState;
import org.evenmorefish.dimensionfishing.enums.FishingState;
import org.evenmorefish.dimensionfishing.events.LavaFishCaughtEvent;
import org.evenmorefish.dimensionfishing.events.VoidFishCaughtEvent;
import org.evenmorefish.dimensionfishing.util.Keys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Random;

public class TrackedHook {

    private static final Random RANDOM = new Random();

    private final FishHook hook;
    private final Player player;
    private LureTracker lureTracker;

    private FishingState fishingState = FishingState.NONE;
    private CatchState catchState = CatchState.WAIT;
    private ArmorStand stand;
    private boolean shouldCustomTick = true;
    private final int voidRequiredLevel;

    private int waitTime = 40;
    private int lureTime = 40;
    private int catchTime = 40;

    // The duration of ticks the hook should be "pulled" by a fish for.
    private boolean pulled = false;
    private int pullTime = 5;

    public TrackedHook(@NotNull Player player, @NotNull FishHook hook) {
        this.player = player;
        this.hook = hook;
        this.voidRequiredLevel = player.getLocation().getBlockY() - RANDOM.nextInt(4, 6);
    }

    public @NotNull FishHook getFishHook() {
        return this.hook;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @Nullable ArmorStand getStand() {
        return this.stand;
    }

    public @NotNull FishingState getFishingState() {
        return this.fishingState;
    }

    public @NotNull CatchState getCatchState() {
        return this.catchState;
    }

    public void tick() {
        if (!shouldCustomTick) {
            return;
        }
        if (hook.getOwnerUniqueId() == null || !hook.isValid() || hook.isOnGround() || isHookedToNormalEntity()) {
            this.shouldCustomTick = false;
            return;
        }

        if (pulled) {
            if (pullTime > 0) {
                pullTime--;
            } else {
                stand.teleport(stand.getLocation().add(0, 0.3, 0));
                pulled = false;
            }
        }

        switch (fishingState) {
            case NONE -> {
                if (isLava()) {
                    attachToStand();
                    this.fishingState = FishingState.LAVA;
                    this.lureTracker = new LureTracker(this);
                    player.sendPlainMessage("You are now lava fishing.");
                } else if (isVoid()) {
                    attachToStand();
                    this.fishingState = FishingState.VOID;
                    this.lureTracker = new LureTracker(this);
                    player.sendPlainMessage("You are now void fishing.");
                } else if (isWater()) {
                    // If in water, vanilla will handle it.
                    this.shouldCustomTick = false;
                }
            }
            case LAVA, VOID -> {
                switch (catchState) {
                    case WAIT -> {
                        waitTime--;
                        if (waitTime <= 0) {
                            player.sendPlainMessage("Fish is being lured.");
                            catchState = CatchState.LURE;
                        }
                    }
                    case LURE -> {
                        if (lureTracker != null) {
                            lureTracker.tick();
                        }
                        lureTime--;
                        if (lureTime <= 0) {
                            player.sendPlainMessage("You can now catch the fish.");
                            switch (fishingState) {
                                case LAVA -> player.playSound(MainConfig.getInstance().getLavaFishingSplashSound());
                                case VOID -> player.playSound(MainConfig.getInstance().getVoidFishingSplashSound());
                            }
                            stand.teleport(stand.getLocation().add(0, -0.3, 0));
                            pulled = true;
                            catchState = CatchState.CATCH;
                        }
                    }
                    case CATCH -> {
                        catchTime--;
                        if (catchTime <= 0) {
                            switch (fishingState) {
                                case LAVA -> {
                                    player.sendPlainMessage("Your hook was swallowed by the lava.");
                                    player.playSound(MainConfig.getInstance().getLavaFishingSwallowSound());
                                }
                                case VOID -> {
                                    player.sendPlainMessage("Your hook was swallowed by the void.");
                                    player.playSound(MainConfig.getInstance().getVoidFishingSwallowSound());
                                }
                            }
                            invalidate();
                        }
                    }
                }
            }
        }
    }

    public void reel() {
        this.shouldCustomTick = false;
        this.invalidate();
        if (catchState != CatchState.CATCH) {
            player.sendPlainMessage("Fish was not ready to be caught.");
            return;
        }
        player.sendPlainMessage("Successfully caught fish. Firing events.");
        switch (fishingState) {
            case LAVA -> new LavaFishCaughtEvent(this).callEvent();
            case VOID -> new VoidFishCaughtEvent(this).callEvent();
            case NONE -> {}
        }
    }

    private boolean isHookedToNormalEntity() {
        Entity hooked = hook.getHookedEntity();
        if (hooked == null) {
            return false;
        }
        return !hooked.equals(stand);
    }

    private boolean isLava() {
        System.out.println(canLavaFish() ? "Can lava fish" : "Cannot lava fish");
        System.out.println(hook.getLocation().getBlock().getType() == Material.LAVA ? "Was lava" : "Was not lava");
        return canLavaFish() && hook.getLocation().getBlock().getType() == Material.LAVA;
    }

    private boolean isWater() {
        System.out.println(hook.getLocation().getBlock().getType() == Material.WATER ? "Was water" : "Was not water");
        return hook.getLocation().getBlock().getType() == Material.WATER;
    }

    private boolean isVoid() {
        System.out.println(canVoidFish() ? "Can void fish" : "Cannot void fish");
        System.out.println(hook.getLocation().getBlockY() <= voidRequiredLevel ? "Was void" : "Was not void");
        return canVoidFish() && hook.getLocation().getBlockY() <= voidRequiredLevel;
    }

    private boolean canLavaFish() {
        return MainConfig.getInstance().getLavaAllowedWorlds().contains(hook.getWorld().getName());
    }

    private boolean canVoidFish() {
        return MainConfig.getInstance().getVoidAllowedWorlds().contains(hook.getWorld().getName());
    }

    public void invalidate() {
        this.shouldCustomTick = false;
        stand.remove();
        hook.remove();
    }

    public boolean shouldStartCustomTick() {
        return canLavaFish() || canVoidFish();
    }

    public boolean isShouldCustomTick() {
        return this.shouldCustomTick;
    }

    public int getRemainingWaitTime() {
        return this.waitTime;
    }

    public int getRemainingLureTime() {
        return this.lureTime;
    }

    public int getRemainingCatchTime() {
        return this.catchTime;
    }

    // ArmorStand shenanigans

    private void attachToStand() {
        if (this.stand != null) {
            System.out.println("Stand already existed?");
            return;
        }
        System.out.println("Spawning armor stand");
        ArmorStand stand = createNewStand(hook.getLocation());
        hook.setHookedEntity(stand);
        stand.addPassenger(hook);
        hook.setVisualFire(false);
        hook.setFireTicks(0);
        this.stand = stand;
        Bukkit.broadcast(Component.text("Hook is now attached to armor stand."));
    }

    private ArmorStand createNewStand(@NonNull Location location) {
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setVisualFire(false);
        stand.setPersistent(false);
        stand.setGravity(false);
        stand.setAI(false);
        stand.getPersistentDataContainer().set(
            Keys.STAND_KEY,
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
