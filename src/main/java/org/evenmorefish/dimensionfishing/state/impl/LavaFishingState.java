package org.evenmorefish.dimensionfishing.state.impl;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.events.LavaFishCaughtEvent;
import org.evenmorefish.dimensionfishing.state.FishingState;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class LavaFishingState implements FishingState {

    @Override
    public void playBiteSound(@NonNull Player player) {
        player.playSound(MainConfig.getInstance().getLavaFishingBiteSound());
    }

    @Override
    public void playSwallowSound(@NonNull Player player) {
        player.playSound(MainConfig.getInstance().getLavaFishingSwallowSound());
    }

    @Override
    public @NonNull ParticleFactory getLureParticles() {
        return MainConfig.getInstance().getLavaFishingLureParticles();
    }

    @Override
    public void callEvent(@NonNull TrackedHook hook) {
        new LavaFishCaughtEvent(hook).callEvent();
    }

    @Override
    public boolean checkPermission(@NonNull Player player) {
        String permission = MainConfig.getInstance().getLavaFishingPermission();
        return permission == null || player.hasPermission(permission);
    }

    @Override
    public boolean checkWorld(@NonNull World hookWorld) {
        List<String> worlds = MainConfig.getInstance().getLavaAllowedWorlds();
        if (worlds.isEmpty()) {
            return hookWorld.getEnvironment() == World.Environment.NETHER;
        }
        return worlds.stream()
            .map(this::parseWorld)
            .anyMatch(hookWorld::equals);
    }


}
