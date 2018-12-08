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
    <artifactId>LandLord</artifactId>
    <version>0.1</version>
    <scope>system</scope>
    <systemPath>/home/alex/Repositories/LandLord-DynMap/target/Testserver/plugins/Landlord-latest.jar</systemPath>
</dependency>
```


Compilation
================

LandLord uses maven as dependency resolver. Execute the maven goal "package" to get a jar, that contains a compiled jar with all dependencies.
In case you want to dive deeper into LandLord development, you can change the output path of the shade plugin directly into your server plugins folder (like I did).

Contributing
============
1. Clone the repository 
2. Change code
3. Submit a pullrequest