package org.evenmorefish.dimensionfishing.util;

import com.destroystokyo.paper.ParticleBuilder;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds particles from a config file.
 */
public class ParticleFactory {

    private final Section section;
    private final List<ParticleBuilder> particles = new ArrayList<>();

    public ParticleFactory(@NotNull Section section) {
        this.section = section;
        loadParticles();
    }

    private void loadParticles() {
        this.particles.clear();
        List<Map<?, ?>> lava = section.getMapList("lava");
        lava.forEach(map -> {
            Particle particle = fetchParticle(map.get("particle"));
            if (particle == null) {
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
            return Particle.valueOf(name.toString());
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
            int rgb = Integer.parseInt(color.toString(), 16);
            return Color.fromRGB(rgb);
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
