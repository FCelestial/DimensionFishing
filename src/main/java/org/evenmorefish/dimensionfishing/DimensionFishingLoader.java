package org.evenmorefish.dimensionfishing;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.evenmorefish.dimensionfishing.config.DimensionFishingConfigProvider;
import org.evenmorefish.dimensionfishing.events.LavaFishCaughtEvent;
import org.evenmorefish.dimensionfishing.events.VoidFishCaughtEvent;
import org.evenmorefish.dimensionfishing.util.ParticleFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DimensionFishingLoader extends JavaPlugin implements DimensionFishingConfigProvider, Listener {

    private DimensionFishing dimensionFishing;
    private ParticleFactory lavaParticles;
    private ParticleFactory voidParticles;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadParticles();
        this.dimensionFishing = new DimensionFishing(this, this);
        this.dimensionFishing.load();
        this.dimensionFishing.enable();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("DimensionFishing loaded successfully!");
    }

    @Override
    public void onDisable() {
        if (this.dimensionFishing != null) {
            this.dimensionFishing.disable();
        }
    }

    private void loadParticles() {
        this.lavaParticles = loadParticleConfig("lava.lure-particles");
        this.voidParticles = loadParticleConfig("void.lure-particles");
    }

    private ParticleFactory loadParticleConfig(String path) {
        List<Map<?, ?>> raw = getConfig().getMapList(path);
        if (raw.isEmpty()) {
            return new ParticleFactory(List.of());
        }
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> converted = (List<Map<?, ?>>) (List<?>) raw;
        return new ParticleFactory(converted);
    }

    private Sound parseSound(String configValue, Sound.Source source, float defaultVol, float defaultPitch) {
        if (configValue == null || configValue.isEmpty()) {
            return null;
        }
        String[] parts = configValue.split(",");
        String soundName = parts[0].trim();
        float volume = parts.length > 1 ? Float.parseFloat(parts[1].trim()) : defaultVol;
        float pitch = parts.length > 2 ? Float.parseFloat(parts[2].trim()) : defaultPitch;
        try {
            return Sound.sound(org.bukkit.Sound.valueOf(soundName), source, volume, pitch);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid sound: " + soundName + ", using fallback");
            return null;
        }
    }

    @EventHandler
    public void onLavaFishCaught(LavaFishCaughtEvent event) {
        Player player = event.getTrackedHook().getPlayer();
        Location loc = player.getLocation();
        Item item = loc.getWorld().dropItem(loc, new ItemStack(Material.COD));
        item.setPickupDelay(Integer.MAX_VALUE);
        PlayerFishEvent fishEvent = new PlayerFishEvent(player, item, event.getTrackedHook().getFishHook(), PlayerFishEvent.State.CAUGHT_FISH);
        Bukkit.getPluginManager().callEvent(fishEvent);
        if (item.isValid()) item.remove();
    }

    @EventHandler
    public void onVoidFishCaught(VoidFishCaughtEvent event) {
        Player player = event.getTrackedHook().getPlayer();
        Location loc = player.getLocation();
        Item item = loc.getWorld().dropItem(loc, new ItemStack(Material.COD));
        item.setPickupDelay(Integer.MAX_VALUE);
        PlayerFishEvent fishEvent = new PlayerFishEvent(player, item, event.getTrackedHook().getFishHook(), PlayerFishEvent.State.CAUGHT_FISH);
        Bukkit.getPluginManager().callEvent(fishEvent);
        if (item.isValid()) item.remove();
    }

    @Override public boolean isLavaEnabled() { return true; }
    @Override public boolean isVoidEnabled() { return true; }

    @Override
    public @NonNull List<String> getLavaAllowedWorlds() {
        return getConfig().getStringList("lava.allowed-worlds");
    }

    @Override
    public @NonNull List<String> getVoidAllowedWorlds() {
        return getConfig().getStringList("void.allowed-worlds");
    }

    @Override
    public @NonNull Sound getLavaFishingSwallowSound() {
        Sound s = parseSound(getConfig().getString("lava.swallow-sound", "BLOCK_LAVA_EXTINGUISH"), Sound.Source.BLOCK, 1.0f, 1.0f);
        return s != null ? s : Sound.sound(org.bukkit.Sound.BLOCK_LAVA_EXTINGUISH, Sound.Source.BLOCK, 1.0f, 1.0f);
    }

    @Override
    public @NonNull Sound getVoidFishingSwallowSound() {
        Sound s = parseSound(getConfig().getString("void.swallow-sound", "ENTITY_ENDERMAN_TELEPORT"), Sound.Source.AMBIENT, 0.25f, 0.1f);
        return s != null ? s : Sound.sound(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, Sound.Source.AMBIENT, 0.25f, 0.1f);
    }

    @Override
    public @NonNull Sound getLavaFishingBiteSound() {
        Sound s = parseSound(getConfig().getString("lava.bite-sound", "ENTITY_FISHING_BOBBER_SPLASH"), Sound.Source.BLOCK, 0.25f, 0.5f);
        return s != null ? s : Sound.sound(org.bukkit.Sound.ENTITY_FISHING_BOBBER_SPLASH, Sound.Source.BLOCK, 0.25f, 0.5f);
    }

    @Override
    public @NonNull Sound getVoidFishingBiteSound() {
        Sound s = parseSound(getConfig().getString("void.bite-sound", "ENTITY_FOX_BITE"), Sound.Source.AMBIENT, 0.25f, 0.1f);
        return s != null ? s : Sound.sound(org.bukkit.Sound.ENTITY_FOX_BITE, Sound.Source.AMBIENT, 0.25f, 0.1f);
    }

    @Override @Nullable public String getLavaFishingPermission() {
        String perm = getConfig().getString("lava.permission", "");
        return perm.isEmpty() ? null : perm;
    }

    @Override @Nullable public String getVoidFishingPermission() {
        String perm = getConfig().getString("void.permission", "");
        return perm.isEmpty() ? null : perm;
    }

    @Override
    public @NonNull ParticleFactory getLavaFishingLureParticles() {
        return lavaParticles != null ? lavaParticles : new ParticleFactory(List.of());
    }

    @Override
    public @NonNull ParticleFactory getVoidFishingLureParticles() {
        return voidParticles != null ? voidParticles : new ParticleFactory(List.of());
    }

    @Override
    public @NonNull Predicate<Player> getLavaPredicate() {
        return player -> {
            var item = player.getInventory().getItemInMainHand();
            if (item.getType() != org.bukkit.Material.FISHING_ROD) return false;
            var meta = item.getItemMeta();
            if (meta == null || !meta.hasCustomModelData()) return false;
            int cmd = meta.getCustomModelData();
            return cmd == 204 || cmd == 205;
        };
    }

    @Override
    public @NonNull Predicate<Player> getVoidPredicate() {
        return player -> {
            var item = player.getInventory().getItemInMainHand();
            if (item.getType() != org.bukkit.Material.FISHING_ROD) return false;
            var meta = item.getItemMeta();
            if (meta == null || !meta.hasCustomModelData()) return false;
            int cmd = meta.getCustomModelData();
            return cmd == 203 || cmd == 205;
        };
    }
}
