package org.evenmorefish.dimensionfishing.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DirectionUtil {

    public static Location forwardFlat(@NotNull Location location, double distance) {
        Location loc = location.clone();
        loc.setPitch(0);
        Vector vector = loc.getDirection().multiply(distance);
        return location.clone().add(vector);
    }

}
