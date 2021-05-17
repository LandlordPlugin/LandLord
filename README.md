![GitHub Workflow Status](https://img.shields.io/github/workflow/status/LandlordPlugin/Landlord/Publish%20to%20Nexus?style=flat-square)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/biz.princeps/landlord-core?label=EldoNexus&nexusVersion=3&server=https%3A%2F%2Feldonexus.de&style=flat-square)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/LandlordPlugin/Landlord/Verify%20state?style=flat-square)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/biz.princeps/landlord-core?color=orange&label=EldoNexus&server=https%3A%2F%2Feldonexus.de&style=flat-square)

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
    implementation("biz.princeps", "eldo-util", "version")
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
    <artifactId>eldo-util</artifactId>
    <version>version</version>
</dependency>
```

Wiki
====
There is a very in depth documentation about nearly everything in the
[wiki](https://github.com/LandlordPlugin/LandLord/wiki).

Compilation
================

LandLord uses maven as dependency resolver. Execute the maven goal "package" to get a jar, that contains all
dependencies. The jars will be located in target folder of their respect version (1.13.2+ in latest, 1.12.2 in legacy).

Contribution
============
[link](https://github.com/LandlordPlugin/LandLord/blob/master/CONTRIBUTING.md)