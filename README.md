Landlord
========

Landlord is a bukkit plugin for players to prevent against griefing, stealing, and animal kills in a simplified manner.  
It is hosted on [spigotmc.org](https://www.spigotmc.org/resources/beta-landlord-2.44398/).

Maven
=====
Replace the version with the current version number!
```xml
<repository>
    <id>princepsrepo</id>
    <url>http://princeps.biz:8081/nexus/content/repositories/princepsrepo/</url>
</repository>

<dependency>
    <groupId>biz.princeps</groupId>
    <artifactId>LandLord</artifactId>
    <version>3.138</version>
    <scope>provided</scope>
</dependency>
```


Compilation
================

LandLord uses maven as dependency resolver. Execute the maven goal "package" to get a jar, that contains all dependencies.
In case you want to dive deeper into LandLord development, you can change the output path of the shade plugin directly into your server plugins folder (like I did).
The setup.sh script will install a testserver in the target folder.

Contributing
============
1. Clone the repository 
2. Execute setup.sh for installing the testserver with all dependencies
3. Testserver is available in the target folder (excluded from git)
4. Change code
5. Submit a pull-request