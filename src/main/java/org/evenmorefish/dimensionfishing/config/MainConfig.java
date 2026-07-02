package org.evenmorefish.dimensionfishing.config;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.evenmorefish.dimensionfishing.util.serializer.SoundSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MainConfig {

    private static MainConfig INSTANCE;

    private final DimensionFishing plugin;
    private final File configFile;
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode config = CommentedConfigurationNode.root();

    private ParticleFactory lavaLureParticles;
    private ParticleFactory voidLureParticles;

    public MainConfig(@NotNull DimensionFishing plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.loader = YamlConfigurationLoader.builder()
            .file(configFile)
            .build();
        INSTANCE = this;
    }

    public void load() {
        reload();
    }

    public void reload() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        try {
            this.config = loader.load();
        } catch (ConfigurateException exception) {
            plugin.getLogger().warning("Failed to load config.yml.");
        }
        this.lavaLureParticles = new ParticleFactory(fetchMapList("lava", "lure-particles"));
        this.voidLureParticles = new ParticleFactory(fetchMapList("void", "lure-particles"));
    }

    public static @NotNull MainConfig getInstance() {
        if (INSTANCE == null || INSTANCE.config == null) {
            throw new IllegalStateException("MainConfig is not loaded.");
        }
        return INSTANCE;
    }

    private List<Map<String, String>> fetchMapList(Object... path) {
        ConfigurationNode node = config.node(path);
        TypeToken<Map<String, String>> typeToken = new TypeToken<>() {};
        try {
            return node.getList(typeToken, List.of());
        } catch (ConfigurateException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to fetch map at " + Arrays.toString(path), exception);
            return List.of();
        }
    }

    public @NotNull Component getReloadMessage() {
        String message = config.node("reload-message").getString("<gradient:#E6F9FF:#8FD9FB>[DimensionFishing] <white>Successfully reloaded the plugin.");
        return MiniMessage.miniMessage().deserialize(message);
    }

    public List<String> getLavaAllowedWorlds() {
        try {
            return config.node("lava", "allowed-worlds").getList(String.class);
        } catch (ConfigurateException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to fetch lava fishing allowed worlds.", exception);
            return List.of();
        }
    }

    public List<String> getVoidAllowedWorlds() {
        try {
            return config.node("void", "allowed-worlds").getList(String.class);
        } catch (ConfigurateException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to fetch void fishing allowed worlds.", exception);
            return List.of();
        }
    }

    public Sound getLavaFishingSwallowSound() {
        Sound sound = SoundSerializer.deserialize(config.node("lava", "swallow-sound").getString());
        if (sound != null) {
            return sound;
        }
        return Sound.sound().type(org.bukkit.Sound.BLOCK_LAVA_EXTINGUISH).build();
    }

    public Sound getVoidFishingSwallowSound() {
        Sound sound = SoundSerializer.deserialize(config.node("void", "swallow-sound").getString());
        if (sound != null) {
            return sound;
        }
        return Sound.sound().type(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT).build();
    }

    public Sound getLavaFishingBiteSound() {
        Sound sound = SoundSerializer.deserialize(config.node("lava", "bite-sound").getString());
        if (sound != null) {
            return sound;
        }
        return Sound.sound()
            .type(org.bukkit.Sound.ENTITY_FISHING_BOBBER_SPLASH)
            .volume(0.25F)
            .pitch(0.5F)
            .build();
    }

    public Sound getVoidFishingBiteSound() {
        Sound sound = SoundSerializer.deserialize(config.node("void", "bite-sound").getString());
        if (sound != null) {
            return sound;
        }
        return Sound.sound()
            .type(org.bukkit.Sound.ENTITY_FOX_BITE)
            .volume(0.25F)
            .pitch(0.1F)
            .build();
    }

    public String getLavaFishingPermission() {
        return config.node("lava", "permission").getString();
    }

    public String getVoidFishingPermission() {
        return config.node("void", "permission").getString();
    }

    public ParticleFactory getLavaFishingLureParticles() {
        return this.lavaLureParticles;
    }

    public ParticleFactory getVoidFishingLureParticles() {
        return this.voidLureParticles;
    }

}
