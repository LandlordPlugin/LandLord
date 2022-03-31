plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("biz.princeps.library-conventions")
}

dependencies {
    implementation(project(":LandLord-core"))
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.7-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5-SNAPSHOT")
    compileOnly("io.papermc:paperlib:1.0.7")
}

description = "LandLord-latest"

val shadebade = project.group as String + ".landlord."

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "version" to PublishData(project).getVersion(true)
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    shadowJar {
        relocate("com.zaxxer", shadebade + "hikari")
        relocate("org.slf4j.slf4j-api", shadebade + "slf4j-api")
        relocate("io.github", shadebade + "nbteditor")
        relocate("de.eldoria.eldoutilities", shadebade + "eldoutilities")
        relocate("io.papermc.lib", shadebade + "paperlib")
        mergeServiceFiles()
        archiveBaseName.set(project.parent?.name)
    }

    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}