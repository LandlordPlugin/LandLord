plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("biz.princeps.library-conventions")
}

dependencies {
    implementation(project(":LandLord-core"))
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.4-SNAPSHOT")
    compileOnly("io.papermc:paperlib:1.0.7")
}

description = "LandLord-legacy"

val shadebade = project.group as String + ".landlord."

publishData {
    useEldoNexusRepos()
    publishComponent("java")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            publishData.configurePublication(this)
        }
    }

    repositories {
        maven {
            name = "EldoNexus"
            url = uri(publishData.getRepository())

            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "version" to publishData.getVersion(true) as String
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
