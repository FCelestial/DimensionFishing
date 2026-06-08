package org.evenmorefish.dimensionfishing.util;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * Builds particles from a config file.
 */
public class ParticleFactory {

    private final List<ParticleBuilder> particles = new ArrayList<>();

    public ParticleFactory(@NotNull List<Map<?, ?>> mapList) {
        loadParticles(mapList);
    }

    private void loadParticles(List<Map<?, ?>> mapList) {
        this.particles.clear();
        mapList.forEach(map -> {
            Particle particle = fetchParticle(map.get("particle"));
            if (particle == null) {
                System.out.println("Invalid particle :(");
                return;
            }
            Color color = fetchColor(map.get("color"));
            int amount = fetchAmount(map.get("amount"));
            this.particles.add(
                new ParticleBuilder(particle)
                    .color(color)
                    .count(amount)
            );
        });
    }

    public void show(@NotNull Location location) {
        particles.forEach(particle ->
            particle.clone().location(location).spawn()
        );
    }

    private @Nullable Particle fetchParticle(@Nullable Object name) {
        if (name == null) {
            return null;
        }
        try {
            return Particle.valueOf(name.toString().toUpperCase());
        } catch (IllegalArgumentException exception) {
            System.out.println(name + " is not a valid particle.");
            return null;
        }
    }

    private @Nullable Color fetchColor(@Nullable Object color) {
        if (color == null) {
            return null;
        }
        try {
            java.awt.Color awtColor = java.awt.Color.decode(color.toString());
            int r = awtColor.getRed();
            int g = awtColor.getGreen();
            int b = awtColor.getBlue();
            return Color.fromRGB(r, g, b);
        } catch (NumberFormatException exception) {
            System.out.println(color + " is not a valid hex color.");
            return null;
        }
    }

    private int fetchAmount(@Nullable Object amount) {
        if (amount == null) {
            return 1;
        }
        try {
            return Integer.parseInt(amount.toString());
        } catch (NumberFormatException exception) {
            return 1;
        }
    }

}
