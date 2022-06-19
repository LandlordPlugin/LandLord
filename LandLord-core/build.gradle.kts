plugins {
    id("biz.princeps.library-conventions")
}

dependencies {
    api(project(":LandLord-api"))
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("io.github.bananapuncher714:nbteditor:7.18.3")
    implementation("de.eldoria:eldo-util:1.11.0-DEV")
    implementation("io.papermc:paperlib:1.0.7")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.TownyAdvanced:Towny:0.98.2.4")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.mojang:authlib:1.5.25")
}

description = "LandLord-core"

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
