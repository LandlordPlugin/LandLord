plugins {
    `java-library`
    `maven-publish`
    id("biz.princeps.java-conventions")
}

publishing {
    val publishData = PublishData(project)

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group as String?
            artifactId = project.name.toLowerCase()
            version = publishData.getVersion()
        }
    }

    repositories {
        maven {
            val release = "https://eldonexus.de/repository/maven-releases/"
            val snapshot = "https://eldonexus.de/repository/maven-snapshots/"
            name = "EldoNexus"
            url = uri(if (publishData.isSnapshot()) snapshot else release)

            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
}