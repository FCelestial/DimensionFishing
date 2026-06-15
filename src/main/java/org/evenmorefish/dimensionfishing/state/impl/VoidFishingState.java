package org.evenmorefish.dimensionfishing.state.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.events.VoidFishCaughtEvent;
import org.evenmorefish.dimensionfishing.state.FishingState;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class VoidFishingState implements FishingState {

    @Override
    public void playBiteSound(@NonNull Player player) {
        player.playSound(MainConfig.getInstance().getVoidFishingBiteSound());
    }

    @Override
    public void playSwallowSound(@NonNull Player player) {
        player.playSound(MainConfig.getInstance().getVoidFishingSwallowSound());
    }

    @Override
    public @NonNull ParticleFactory getLureParticles() {
        return MainConfig.getInstance().getVoidFishingLureParticles();
    }

    @Override
    public void callEvent(@NonNull TrackedHook hook) {
        new VoidFishCaughtEvent(hook).callEvent();
    }

    @Override
    public boolean checkPermission(@NonNull Player player) {
        String permission = MainConfig.getInstance().getVoidFishingPermission();
        return permission == null || player.hasPermission(permission);
    }

    @Override
    public boolean checkWorld(@NonNull World hookWorld) {
        List<String> worlds = MainConfig.getInstance().getVoidAllowedWorlds();
        if (worlds.isEmpty()) {
            return hookWorld.getEnvironment() == World.Environment.THE_END;
        }
        return worlds.stream()
            .map(this::parseWorld)
            .anyMatch(hookWorld::equals);
    }

}
