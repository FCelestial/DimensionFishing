package org.evenmorefish.dimensionfishing.config;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.evenmorefish.dimensionfishing.util.serializer.SoundSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainConfig {

    private static MainConfig INSTANCE;

    private final DimensionFishing plugin;
    private FileConfiguration config;

    private ParticleFactory lavaLureParticles;
    private ParticleFactory voidLureParticles;

    public MainConfig(@NotNull DimensionFishing plugin) {
        this.plugin = plugin;
        INSTANCE = this;
    }

    public void load() {
        plugin.saveDefaultConfig();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.lavaLureParticles = new ParticleFactory(config.getMapList("lava.lure-particles"));
        this.voidLureParticles = new ParticleFactory(config.getMapList("void.lure-particles"));
    }

    public static @NotNull MainConfig getInstance() {
        if (INSTANCE == null || INSTANCE.config == null) {
            throw new IllegalStateException("MainConfig is not loaded.");
        }
        return INSTANCE;
    }

    public @NotNull Component getReloadMessage() {
        String message = config.getString(
            "reload-message",
            "<gradient:#E6F9FF:#8FD9FB>[DimensionFishing] <white>Successfully reloaded the plugin."
        );
        return MiniMessage.miniMessage().deserialize(message);
    }

    public List<String> getLavaAllowedWorlds() {
        return config.getStringList("lava.allowed-worlds");
    }

    public List<String> getVoidAllowedWorlds() {
        return config.getStringList("void.allowed-worlds");
    }

    public Sound getLavaFishingSwallowSound() {
        Sound sound = SoundSerializer.deserialize(config.getString("lava.swallow-sound"));
        if (sound != null) {
            return sound;
        }
        return Sound.sound().type(org.bukkit.Sound.BLOCK_LAVA_EXTINGUISH).build();
    }

    public Sound getVoidFishingSwallowSound() {
        Sound sound = SoundSerializer.deserialize(config.getString("void.swallow-sound"));
        if (sound != null) {
            return sound;
        }
        return Sound.sound().type(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT).build();
    }

    public Sound getLavaFishingBiteSound() {
        Sound sound = SoundSerializer.deserialize(config.getString("lava.bite-sound"));
        if (sound != null) {
            return sound;
        }
        return Sound.sound()
            .type(org.bukkit.Sound.ENTITY_FISHING_BOBBER_SPLASH)
            .volume(0.25F)
            .pitch(0.5F)
            .build();
    }

    public String getLavaFishingPermission() {
        return config.getString("lava.permission", null);
    }

    public String getVoidFishingPermission() {
        return config.getString("void.permission", null);
    }

    public Sound getVoidFishingBiteSound() {
        Sound sound = SoundSerializer.deserialize(config.getString("void.bite-sound"));
        if (sound != null) {
            return sound;
        }
        return Sound.sound()
            .type(org.bukkit.Sound.ENTITY_FISHING_BOBBER_SPLASH)
            .volume(0.25F)
            .pitch(0.1F)
            .build();
    }

    public ParticleFactory getLavaFishingLureParticles() {
        return this.lavaLureParticles;
    }

    public ParticleFactory getVoidFishingLureParticles() {
        return this.voidLureParticles;
    }

}
