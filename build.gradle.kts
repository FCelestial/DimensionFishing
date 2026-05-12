plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
}

group = "org.evenmorefish"
version = properties["project-version"] as String
description = "Experimental plugin that allows void and lava fishing"
java.sourceCompatibility = JavaVersion.VERSION_21

bukkit {
    name = project.name
    version = project.version.toString()
    main = "org.evenmorefish.dimensionfishing.DimensionFishing"
    apiVersion = "1.21"
    author = "FireML"
    description = project.description.toString()

    paperPluginLoader = "org.evenmorefish.dimensionfishing.LibraryLoader"
    paperSkipLibraries = true
    generateLibrariesJson = true
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    generateBukkitPluginDescription {
        useGoogleMavenCentralProxy()
    }
}
