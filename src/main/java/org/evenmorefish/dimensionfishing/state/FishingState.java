package org.evenmorefish.dimensionfishing.state;

import org.bukkit.entity.Player;
import org.evenmorefish.dimensionfishing.common.TrackedHook;
import org.evenmorefish.dimensionfishing.state.impl.LavaFishingState;
import org.evenmorefish.dimensionfishing.state.impl.NoneFishingState;
import org.evenmorefish.dimensionfishing.state.impl.VoidFishingState;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface FishingState {

    NoneFishingState NONE = new NoneFishingState();
    LavaFishingState LAVA = new LavaFishingState();
    VoidFishingState VOID = new VoidFishingState();

    void playSplashSound(@NotNull Player player);

    void playSwallowSound(@NotNull Player player);

    @NotNull ParticleFactory getLureParticles();

    void callEvent(@NotNull TrackedHook hook);

}
