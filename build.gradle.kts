import de.chojo.PublishData
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    // Only applied to the subprojects below; the root project publishes nothing and would fail
    // generateBuildData because it never registers a repo via useEldoNexusRepos().
    id("de.chojo.publishdata") version "1.4.0" apply false
    // NOTE: RunServer fails task validation on Gradle 9.7+ ("downloadPlugins.elements is missing an
    // input or output annotation"), so the wrapper is pinned to 9.6.1. Bump both once
    // https://github.com/jpenilla/run-task/issues/158 is released.
    id("xyz.jpenilla.run-paper") version "3.0.2"
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
}
