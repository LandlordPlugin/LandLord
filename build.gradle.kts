import de.chojo.PublishData
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    id("de.chojo.publishdata") version "1.4.0" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "biz.princeps"
version = "5.0.0"

subprojects {
    apply {
        plugin<PublishData>()
    }
}

val toolchains = extensions.getByType<JavaToolchainService>()

tasks {
    register<RunServer>("runLatest") {
        // 26.1+ needs Java 25 or newer
        javaLauncher = toolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
        }
        minecraftVersion("26.2")
        pluginJars(*project(":LandLord-latest").getTasksByName("shadowJar", false).map { (it as Jar).archiveFile }
            .toTypedArray())
        downloadPlugins {
            modrinth("worldguard", "btHBavWa") // 7.0.18
            // worldedit supports multiple platforms, we need to use the specific version id
            // instead of the actual version
            modrinth("fastasyncworldedit", "Ad3NnAQP") // 2.15.3 Paper
        }
        runDirectory = file("run/latest")
        group = "run paper"
    }
    register<RunServer>("runLegacy") {
        // 1.12.2 needs Java 17 or earlier
        javaLauncher = toolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(17)
        }
        minecraftVersion("1.12.2")
        pluginJars(*project(":LandLord-legacy").getTasksByName("shadowJar", false).map { (it as Jar).archiveFile }
            .toTypedArray())
        downloadPlugins {
            url("https://dev.bukkit.org/projects/worldguard/files/2610618/download")
            url("https://dev.bukkit.org/projects/worldedit/files/2597538/download")
        }
        runDirectory = file("run/legacy")
        group = "run paper"
    }
}
