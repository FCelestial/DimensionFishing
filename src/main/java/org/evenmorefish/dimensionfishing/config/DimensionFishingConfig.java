package org.evenmorefish.dimensionfishing.config;

import net.kyori.adventure.sound.Sound;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DimensionFishingConfig {

    private static DimensionFishingConfig INSTANCE;

    private final DimensionFishingConfigProvider provider;

    private ParticleFactory lavaLureParticles;
    private ParticleFactory voidLureParticles;

    public DimensionFishingConfig(@NotNull DimensionFishingConfigProvider provider) {
        this.provider = provider;
        INSTANCE = this;
    }

    public void load() {
        reload();
    }

    public void reload() {
        provider.reloadConfig();
        this.lavaLureParticles = new ParticleFactory(provider.getLavaFishingLureParticles());
        this.voidLureParticles = new ParticleFactory(provider.getVoidFishingLureParticles());
    }

    public static @NotNull DimensionFishingConfig getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("DimensionFishingConfig is not loaded.");
        }
        return INSTANCE;
    }

    public List<String> getLavaAllowedWorlds() {
        return provider.getLavaAllowedWorlds();
    }

    public List<String> getVoidAllowedWorlds() {
        return provider.getVoidAllowedWorlds();
    }

    public Sound getLavaFishingSwallowSound() {
        return provider.getLavaFishingSwallowSound();
    }

    public Sound getVoidFishingSwallowSound() {
        return provider.getVoidFishingSwallowSound();
    }

    public Sound getLavaFishingBiteSound() {
        return provider.getLavaFishingBiteSound();
    }

    public Sound getVoidFishingBiteSound() {
        return provider.getVoidFishingBiteSound();
    }

    public String getLavaFishingPermission() {
        return provider.getLavaFishingPermission();
    }

    public String getVoidFishingPermission() {
        return provider.getVoidFishingPermission();
    }

    public ParticleFactory getLavaFishingLureParticles() {
        return this.lavaLureParticles;
    }

    public ParticleFactory getVoidFishingLureParticles() {
        return this.voidLureParticles;
    }

}
