plugins {
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("biz.princeps.library-conventions")
}

dependencies {
    implementation(project(":LandLord-core"))
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.4-SNAPSHOT")
    //compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0-SNAPSHOT")
}

description = "LandLord-latest"

val shadebade = project.group as String + ".landlord."

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "version" to version
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