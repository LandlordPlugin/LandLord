plugins {
    java
    `java-library`
    `maven-publish`
}

version = rootProject.version

repositories {
    mavenCentral()
    // Spigot & Paper
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    // WorldEdit & WorldGuard
    maven { url = uri("https://maven.enginehub.org/repo/") }
    // Towny
    maven { url = uri("https://jitpack.io") }
    // PAPI
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    // nbteditor & bungeecord-chat
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    // EldoUtilitites & Landlord
    maven { url = uri("https://eldonexus.de/repository/maven-public/") }
    // Authlib
    maven { url = uri("https://papermc.io/repo/repository/maven-releases/") }
}

allprojects {
    java {
        withSourcesJar()
        withJavadocJar()
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
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
