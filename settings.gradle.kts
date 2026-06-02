rootProject.name = "DimensionFishing"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // compileOnly dependencies
            library("paper-api", "io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
            library("evenmorefish", "com.oheers.evenmorefish:even-more-fish-plugin:2.3.4")

            // implementation dependencies

            // paperLibrary dependencies

            // Gradle plugins
            plugin("shadow", "com.gradleup.shadow").version("9.4.1")
            plugin("plugin-yml", "de.eldoria.plugin-yml.bukkit").version("0.9.0")
        }
    }
}
