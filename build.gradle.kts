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
        minecraftVersion("1.21.4")
        pluginJars(*project(":LandLord-latest").getTasksByName("shadowJar", false).map { (it as Jar).archiveFile }
            .toTypedArray())
        downloadPlugins {
            modrinth("worldguard", "f9NoeotB") // 7.0.13
            // worldedit supports multiple platforms, we need to use the specific version id
            // instead of the actual version
            modrinth("fastasyncworldedit", "cf5QSDJ7") // 2.12.3 Paper
        }
        runDirectory = file("run/latest")
        group = "run paper"
    }
}
