rootProject.name = "LandLord"
include(":LandLord-core")
include(":LandLord-latest")
include(":LandLord-legacy")
include(":LandLord-api")

pluginManagement{
    repositories{
        gradlePluginPortal()
        maven("https://eldonexus.de/repository/maven-public/")
    }
}
