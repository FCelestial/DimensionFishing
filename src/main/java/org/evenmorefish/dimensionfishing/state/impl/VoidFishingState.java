package org.evenmorefish.dimensionfishing.state.impl;

import org.bukkit.entity.Player;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.events.VoidFishCaughtEvent;
import org.evenmorefish.dimensionfishing.state.FishingState;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jspecify.annotations.NonNull;

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

}
