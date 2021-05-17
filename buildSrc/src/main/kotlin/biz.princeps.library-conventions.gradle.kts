import org.gradle.kotlin.dsl.get

plugins {
    `java-library`
    `maven-publish`
    id("biz.princeps.java-conventions")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group as String?
            artifactId = project.name.toLowerCase()
            version = project.version as String?
        }

    }

    repositories {
        maven {
            val isSnapshot = version.toString().endsWith("SNAPSHOT");
            val release = "https://eldonexus.de/repository/maven-releases/";
            val snapshot = "https://eldonexus.de/repository/maven-snapshots/";
            name = "EldoNexus"
            url = uri(if (isSnapshot) snapshot else release)

            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
}