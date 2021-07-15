plugins {
    `java-library`
    `maven-publish`
}

group = "biz.princeps"
version = "4.354"

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    // WorldEdit & WorldGuard
    maven { url = uri("https://maven.enginehub.org/repo/") }
    // Towny
    maven { url = uri("https://jitpack.io") }
    // PAPI
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    // nbteditor & bungeecord-chat
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    // EldoUtilitites & Landlord
    maven { url = uri("https://eldonexus.de/repository/maven-releases/") }
    // Authlib
    maven { url = uri("https://papermc.io/repo/repository/maven-releases/") }
}

allprojects {
    java {
        withSourcesJar()
        withJavadocJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks {
    publish {
        dependsOn(build)
    }

    compileJava {
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

