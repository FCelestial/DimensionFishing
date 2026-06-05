package org.evenmorefish.dimensionfishing;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.evenmorefish.dimensionfishing.util.DirectionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LureTracker {

    private static final Random RANDOM = new Random();

    private final ParticleBuilder flameParticle;
    private final ParticleBuilder lavaParticle;

    private final TrackedHook hook;
    private final double distance;
    private final int yaw;
    private final Location startLocation;
    private Location currentLocation;

    public LureTracker(@NotNull TrackedHook hook) {
        this.hook = hook;
        this.distance = RANDOM.nextDouble(4D, 6D);
        this.yaw = RANDOM.nextInt(0, 359);

        Location hookLocation = hook.getFishHook().getLocation().clone();
        hookLocation.setYaw(this.yaw);
        this.startLocation = DirectionUtil.forwardFlat(hookLocation, distance);
        this.currentLocation = this.startLocation;

        // TODO make this configurable
        this.flameParticle = new ParticleBuilder(Particle.FLAME).count(3);
        this.lavaParticle = new ParticleBuilder(Particle.LAVA).count(1);
    }

    public void tick() {
        flameParticle.location(currentLocation).spawn();
        lavaParticle.location(currentLocation).spawn();
        updateLocation();
    }

    private void updateLocation() {
        // TODO update currentLocation based off the amount of lure ticks remaining.
        int remainingTicks = hook.getRemainingLureTime();
        if (remainingTicks <= 0) {
            return;
        }
        Location hookLocation = hook.getFishHook().getLocation();
        hookLocation.setYaw(this.yaw);
        this.currentLocation = DirectionUtil.forwardFlat(hookLocation, remainingTicks / distance);
        /*
        Location hookLocation = hook.getFishHook().getLocation();
        Vector to = hookLocation.toVector().subtract(this.currentLocation.toVector());
        Vector step = to.multiply(1.0 / remainingTicks);
        this.currentLocation = this.currentLocation.clone().add(step);
         */
    }

}
