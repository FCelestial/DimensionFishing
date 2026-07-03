package org.evenmorefish.dimensionfishing.config;

import net.kyori.adventure.sound.Sound;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Provides all configs for DimensionFishing.
 */
public interface DimensionFishingConfigProvider {

    boolean isLavaEnabled();

    boolean isVoidEnabled();

    @NonNull List<String> getLavaAllowedWorlds();

    @NonNull List<String> getVoidAllowedWorlds();

    @NonNull Sound getLavaFishingSwallowSound();

    @NonNull Sound getVoidFishingSwallowSound();

    @NonNull Sound getLavaFishingBiteSound();

    @NonNull Sound getVoidFishingBiteSound();

    @Nullable String getLavaFishingPermission();

    @Nullable String getVoidFishingPermission();

    @NonNull List<Map<?, ?>> getLavaFishingLureParticles();

    @NonNull List<Map<?, ?>> getVoidFishingLureParticles();
    
}
