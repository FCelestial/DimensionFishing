package org.evenmorefish.dimensionfishing.config;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class MainConfig {

    private static final MainConfig instance = new MainConfig();

    private MainConfig() {}

    public static @NonNull MainConfig getInstance() {
        return instance;
    }

    public List<String> getLavaAllowedWorlds() {
        return List.of("world_nether");
    }

    public List<String> getVoidAllowedWorlds() {
        return List.of("world_the_end");
    }

}
