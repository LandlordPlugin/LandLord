Landlord
========

Landlord is a bukkit plugin for players to prevent against griefing, stealing, and animal kills in a simplified manner.  
It is hosted on [spigotmc.org](https://www.spigotmc.org/resources/beta-landlord-2.44398/).

Maven
=====
```xml
<repository>
    <id>princepsrepo</id>
    <url>http://princeps.biz:8081/nexus/content/repositories/princepsrepo/</url>
</repository>

<dependency>
    <groupId>biz.princeps</groupId>
    <artifactId>LandLord-api</artifactId>
    <version>4.0.5</version>
    <scope>provided</scope>
</dependency>
```

Wiki
====
There is a very in depth documentation about nearly everything in the
[wiki](https://gitlab.com/princeps/LandLord/wikis/home).

Compilation
================

LandLord uses maven as dependency resolver. Execute the maven goal "package" to get a jar, that contains all dependencies.
The jars will be located in target folder of their respect version (1.13.2+ in latest, 1.12.2 in legacy). 

Contribution
============
[link](https://gitlab.com/princeps/LandLord/blob/master/CONTRIBUTING.md)