package org.evenmorefish.dimensionfishing.common;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FishHook;
import org.jetbrains.annotations.NotNull;

public record TrackedHook(@NotNull FishingState state, @NotNull FishHook hook, @NotNull ArmorStand armorStand) {

    public void invalidate() {
        hook.remove();
        armorStand.remove();
    }

}
