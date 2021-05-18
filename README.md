![GitHub Workflow Status](https://img.shields.io/github/workflow/status/LandlordPlugin/Landlord/Verify%20state?style=for-the-badge&label=Building)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/LandlordPlugin/Landlord/Publish%20to%20Nexus?style=for-the-badge&label=Publishing)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/biz.princeps/landlord-core?label=Release&nexusVersion=3&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/biz.princeps/landlord-core?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)

Landlord
========

Landlord is a bukkit plugin for players to prevent against griefing, stealing, and animal kills in a simplified
manner.  
It is hosted on [spigotmc.org](https://www.spigotmc.org/resources/44398/).

Maven
=====

Gradle
``` kotlin
repositories {
    maven { url = uri("https://eldonexus.de/repository/maven-releases") }
}

dependencies {
    implementation("biz.princeps", "landlord-core", "version")
}
```

Maven
``` xml
<repository>
    <id>EldoNexus</id>
    <url>https://eldonexus.de/repository/maven-releases/</url>
</repository>

<dependency>
    <groupId>biz.princeps</groupId>
    <artifactId>landlord-core</artifactId>
    <version>version</version>
</dependency>
```

Wiki
====
There is a very in depth documentation about nearly everything in the
[wiki](https://github.com/LandlordPlugin/LandLord/wiki).

Compilation
================

Gradle is the recommended way to build the project. Use `./gradlew build` in the main project directory to build the project.

Contribution
============
[link](https://github.com/LandlordPlugin/LandLord/blob/master/CONTRIBUTING.md)