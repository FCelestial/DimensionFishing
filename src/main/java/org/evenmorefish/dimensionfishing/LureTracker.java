package org.evenmorefish.dimensionfishing;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.util.DirectionUtil;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LureTracker {

    private static final Random RANDOM = new Random();

    private final TrackedHook hook;
    private final double distance;
    private final int yaw;
    private final Location startLocation;
    private Location currentLocation;

    private final ParticleFactory particles;

    public LureTracker(@NotNull TrackedHook hook) {
        this.hook = hook;
        this.distance = RANDOM.nextDouble(4D, 6D);
        this.yaw = RANDOM.nextInt(0, 359);

        Location hookLocation = hook.getFishHook().getLocation().clone();
        hookLocation.setYaw(this.yaw);
        this.startLocation = DirectionUtil.forwardFlat(hookLocation, distance);
        this.currentLocation = this.startLocation;

        this.particles = switch (hook.getFishingState()) {
            case LAVA -> MainConfig.getInstance().getLavaFishingLureParticles();
            case VOID -> MainConfig.getInstance().getVoidFishingLureParticles();
            default -> null;
        };
    }

    public void tick() {
        if (particles != null) {
            particles.show(currentLocation);
        }
        updateLocation();
    }

    private void updateLocation() {
        int remainingTicks = hook.getRemainingLureTime();
        if (remainingTicks <= 0) {
            return;
        }
        Location hookLocation = hook.getFishHook().getLocation();
        hookLocation.setYaw(this.yaw);
        this.currentLocation = DirectionUtil.forwardFlat(hookLocation, remainingTicks / distance).add(0, 0.1, 0);
    }

}
