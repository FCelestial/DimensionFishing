plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml)
}

repositories {
    //mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")

    // EvenMoreFish
    maven("https://repo.codemc.io/repository/EvenMoreFish/")

    // mcMMO
    maven("https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.evenmorefish) {
        exclude("de.tr7zw", "item-nbt-api")
        exclude("com.github.Anon8281", "UniversalScheduler")
    }
    compileOnly(libs.mcmmo) {
        exclude("*", "*")
    }
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
    softDepend = listOf(
        "EvenMoreFish"
    )

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

publishing {
    repositories {
        maven {
            url = uri("https://repo.codemc.io/repository/EvenMoreFish/")

            val mavenUsername = System.getenv("JENKINS_USERNAME")
            val mavenPassword = System.getenv("JENKINS_PASSWORD")

            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}
