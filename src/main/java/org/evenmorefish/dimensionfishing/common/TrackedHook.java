package org.evenmorefish.dimensionfishing.common;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.util.player.UserManager;
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
import org.evenmorefish.dimensionfishing.LureTracker;
import org.evenmorefish.dimensionfishing.state.CatchState;
import org.evenmorefish.dimensionfishing.state.FishingState;
import org.evenmorefish.dimensionfishing.state.impl.NoneFishingState;
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

    private int waitTime = 0;
    private int lureTime = 0;
    private int catchTime = 40;

    // The duration of ticks the hook should be "pulled" by a fish for.
    private boolean pulled = false;
    private int pullTime = 5;

    public TrackedHook(@NotNull Player player, @NotNull FishHook hook, int lureLevel) {
        this.player = player;
        this.hook = hook;
        this.voidRequiredLevel = player.getLocation().getBlockY() - RANDOM.nextInt(4, 6);

        // Calculating lure and wait time. Unfortunately the Bukkit API doesn't apply lure so we have to do it ourselves :D
        this.waitTime = fetchWaitTime(lureLevel);
        this.lureTime = RANDOM.nextInt(hook.getMinLureTime(), hook.getMaxLureTime());
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

        if (fishingState instanceof NoneFishingState) {
            if (isLava()) {
                attachToStand();
                this.fishingState = FishingState.LAVA;
                this.lureTracker = new LureTracker(this);
            } else if (isVoid()) {
                attachToStand();
                this.fishingState = FishingState.VOID;
                this.lureTracker = new LureTracker(this);
            } else if (isWater()) {
                // If in water, vanilla will handle it.
                this.shouldCustomTick = false;
            }
            return;
        }

        switch (catchState) {
            case WAIT -> {
                waitTime--;
                if (waitTime <= 0) {
                    catchState = CatchState.LURE;
                }
            }
            case LURE -> {
                if (lureTracker != null) {
                    lureTracker.tick();
                }
                lureTime--;
                if (lureTime <= 0) {
                    fishingState.playBiteSound(player);
                    stand.teleport(stand.getLocation().add(0, -0.3, 0));
                    pulled = true;
                    catchState = CatchState.CATCH;
                }
            }
            case CATCH -> {
                catchTime--;
                if (catchTime <= 0) {
                    fishingState.playSwallowSound(player);
                    invalidate();
                }
            }
        }
    }

    public void reel() {
        this.shouldCustomTick = false;
        this.invalidate();
        if (catchState != CatchState.CATCH) {
            return;
        }
        if (checkMcMMOOverfishing()) {
            return;
        }
        fishingState.callEvent(this);
    }

    private boolean isHookedToNormalEntity() {
        Entity hooked = hook.getHookedEntity();
        if (hooked == null) {
            return false;
        }
        return !hooked.equals(stand);
    }

    private boolean isLava() {
        return canLavaFish() && hook.getLocation().getBlock().getType() == Material.LAVA;
    }

    private boolean isWater() {
        return hook.getLocation().getBlock().getType() == Material.WATER;
    }

    private boolean isVoid() {
        return canVoidFish() && hook.getLocation().getBlockY() <= voidRequiredLevel;
    }

    private boolean canLavaFish() {
        return FishingState.LAVA.checkPermission(player) && FishingState.LAVA.checkWorld(hook.getWorld());
    }

    private boolean canVoidFish() {
        return FishingState.VOID.checkPermission(player) && FishingState.VOID.checkWorld(hook.getWorld());
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
            return;
        }
        ArmorStand stand = createNewStand(hook.getLocation());
        hook.setHookedEntity(stand);
        stand.addPassenger(hook);
        hook.setVisualFire(false);
        hook.setFireTicks(0);
        this.stand = stand;
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

    private int fetchWaitTime(int lureLevel) {
        int ticksPerLureLevel = 100;
        int reduceTicks = (lureLevel * ticksPerLureLevel);
        int maxWaitTime = Math.max(0, hook.getMaxWaitTime() - reduceTicks);
        int minWaitTime = Math.max(0, hook.getMinWaitTime() - reduceTicks);

        return (minWaitTime == maxWaitTime) ? minWaitTime : RANDOM.nextInt(minWaitTime, maxWaitTime);
    }

    private boolean checkMcMMOOverfishing() {
        if (!Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            return false;
        }
        if (ExperienceConfig.getInstance().isFishingExploitingPrevented()) {
            return false;
        }
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            return false;
        }
        FishingManager manager = mmoPlayer.getFishingManager();
        manager.processExploiting(this.hook.getLocation().toVector());
        if (manager.isExploitingFishing()) {
            // This was taken directly from mcMMO's source code.
            player.sendMessage(LocaleLoader.getString(
                "Fishing.ScarcityTip",
                ExperienceConfig.getInstance().getFishingExploitingOptionMoveRange())
            );
            return true;
        }
        return false;
    }

}
