package org.evenmorefish.dimensionfishing;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.evenmorefish.dimensionfishing.common.FishingListener;
import org.evenmorefish.dimensionfishing.common.HookManager;
import org.evenmorefish.dimensionfishing.hooks.evenmorefish.LavaFishingProcessor;
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
    public void onLoad() {}

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(FishingListener.getInstance(), this);
        HookManager.getInstance().load();

        Bukkit.getPluginManager().registerEvents(new LavaFishingProcessor(), this);
    }

    @Override
    public void onDisable() {
        FishingListener.getInstance().shutdown();
        HookManager.getInstance().shutdown();
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            // Register Brigadier commands here.
            // commands.registrar().register(new MyCommand().get());
        });
    }

}
