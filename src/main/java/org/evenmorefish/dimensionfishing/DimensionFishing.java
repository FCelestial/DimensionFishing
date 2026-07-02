package org.evenmorefish.dimensionfishing;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.evenmorefish.dimensionfishing.common.FishingListener;
import org.evenmorefish.dimensionfishing.common.HookManager;
import org.evenmorefish.dimensionfishing.config.DimensionFishingConfig;
import org.evenmorefish.dimensionfishing.state.FishingState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class DimensionFishing {

    private static DimensionFishing INSTANCE;

    private final JavaPlugin plugin;
    private final Metrics metrics;
    private final Logger logger;

    public DimensionFishing(@NotNull JavaPlugin plugin) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
        this.plugin = plugin;
        this.logger = Logger.getLogger("DimensionFishing via " + plugin.getName());
        this.metrics = new Metrics(plugin, 32045);
    }

    public static @NotNull DimensionFishing getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(DimensionFishing.class.getSimpleName() + " has not been assigned!");
        }
        return INSTANCE;
    }

    public @NotNull JavaPlugin getPlugin() {
        return this.plugin;
    }

    public void enable() {
        HookManager.getInstance().load();
        Bukkit.getPluginManager().registerEvents(new FishingListener(), plugin);
    }

    public void disable() {
        HookManager.getInstance().shutdown();
    }

    public void reload(@Nullable CommandSender sender) {
        DimensionFishingConfig.getInstance().reload();
        if (sender instanceof Player player) {
            FishingState.LAVA.getLureParticles().show(player.getLocation());
            FishingState.VOID.getLureParticles().show(player.getLocation());
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

}
