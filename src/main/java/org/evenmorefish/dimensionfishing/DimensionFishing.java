package org.evenmorefish.dimensionfishing;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.evenmorefish.dimensionfishing.commands.MainCommand;
import org.evenmorefish.dimensionfishing.common.FishingListener;
import org.evenmorefish.dimensionfishing.common.HookManager;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.hooks.evenmorefish.LavaFishingProcessor;
import org.evenmorefish.dimensionfishing.hooks.evenmorefish.VoidFishingProcessor;
import org.jetbrains.annotations.NotNull;

public class DimensionFishing extends JavaPlugin {

    private static DimensionFishing INSTANCE;

    public DimensionFishing() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
    }

    public static @NotNull DimensionFishing getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(DimensionFishing.class.getSimpleName() + " has not been assigned!");
        }
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        registerCommands();
        loadConfig();
    }

    @Override
    public void onEnable() {
        HookManager.getInstance().load();

        Bukkit.getPluginManager().registerEvents(new FishingListener(), this);

        // EvenMoreFish Hook
        Bukkit.getPluginManager().registerEvents(new LavaFishingProcessor(), this);
        Bukkit.getPluginManager().registerEvents(new VoidFishingProcessor(), this);
    }

    @Override
    public void onDisable() {
        HookManager.getInstance().shutdown();
    }

    public void reload() {
        MainConfig.getInstance().reload();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(MainCommand.get());
        });
    }

    private void loadConfig() {
        new MainConfig(this).load();
    }

}
