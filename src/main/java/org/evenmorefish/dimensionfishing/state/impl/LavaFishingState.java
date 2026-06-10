package org.evenmorefish.dimensionfishing.state.impl;

import org.bukkit.entity.Player;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.events.LavaFishCaughtEvent;
import org.evenmorefish.dimensionfishing.state.FishingState;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jspecify.annotations.NonNull;

public class LavaFishingState implements FishingState {

    @Override
    public void playSplashSound(@NonNull Player player) {
        player.playSound(MainConfig.getInstance().getLavaFishingSplashSound());
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

}
