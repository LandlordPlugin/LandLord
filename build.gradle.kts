import de.chojo.PublishData
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    id("de.chojo.publishdata") version "1.2.4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "biz.princeps"
version = "5.0.0"

subprojects {
    apply {
        plugin<PublishData>()
    }
}

tasks {
    register<RunServer>("runLatest") {
        minecraftVersion("1.21.1")
        pluginJars(*project(":LandLord-latest").getTasksByName("shadowJar", false).map { (it as Jar).archiveFile }
            .toTypedArray())
        downloadPlugins {
            modrinth("worldguard", "7.0.12")
            // worldedit supports multiple platforms, we need to use the specific version id
            // instead of the actual version
            modrinth("worldedit", "ecqqLKUO") // 7.3.8
        }
        runDirectory = file("run/latest")
        group = "run paper"
    }
    register<RunServer>("runLegacy") {
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
