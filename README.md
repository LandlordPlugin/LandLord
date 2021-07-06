![GitHub Workflow Status](https://img.shields.io/github/workflow/status/LandlordPlugin/Landlord/Verify%20state?style=for-the-badge&label=Building)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/LandlordPlugin/Landlord/Publish%20to%20Nexus?style=for-the-badge&label=Publishing)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/maven-releases/biz.princeps/landlord-api?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Development)](https://img.shields.io/nexus/maven-dev/biz.princeps/landlord-api?label=DEV&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/biz.princeps/landlord-core?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)

# Landlord
Landlord is a bukkit plugin for players to prevent against griefing, stealing, and animal kills in a simplified
manner.\
It is hosted on [spigotmc.org](https://www.spigotmc.org/resources/44398/).

# Download
You can download the latest builds from our nexus.\
Please search your version below.\
Only use snapshot if you know what you are doing. We dont guarantee that these will run without any problems.

### Latest 1.13+
[Latest Release](https://eldonexus.de/service/rest/v1/search/assets/download?sort=version&direction=desc&&repository=maven-releases&maven.groupId=biz.princeps&maven.artifactId=landlord-latest&maven.classifier=all) \
[Latest Dev](https://eldonexus.de/service/rest/v1/search/assets/download?sort=version&direction=desc&q=DEV&maven.groupId=biz.princeps&maven.artifactId=landlord-latest&maven.classifier=all) \
[Latest Snapshot](https://eldonexus.de/service/rest/v1/search/assets/download?sort=version&direction=desc&q=SNAPSHOT&maven.groupId=biz.princeps&maven.artifactId=landlord-latest&maven.classifier=all)

### Legacy 1.12
[Latest Release](https://eldonexus.de/service/rest/v1/search/assets/download?sort=version&direction=desc&&repository=maven-releases&maven.groupId=biz.princeps&maven.artifactId=landlord-legacy&maven.classifier=all) \
[Latest Dev](https://eldonexus.de/service/rest/v1/search/assets/download?sort=version&direction=desc&q=DEV&maven.groupId=biz.princeps&maven.artifactId=landlord-legacy&maven.classifier=all) \
[Latest Snapshot](https://eldonexus.de/service/rest/v1/search/assets/download?sort=version&direction=desc&q=SNAPSHOT&maven.groupId=biz.princeps&maven.artifactId=landlord-legacy&maven.classifier=all)

# Dependency
If you want to use landlord as a dependency you can these.\
Make sure to replace the version with the release version from above.
### Gradle
``` kotlin
repositories {
    maven { url = uri("https://eldonexus.de/repository/maven-releases") }
}

dependencies {
    implementation("biz.princeps", "landlord-core", "{version}")
}
```

### Maven
``` xml
<repository>
    <id>EldoNexus</id>
    <url>https://eldonexus.de/repository/maven-releases/</url>
</repository>

<dependency>
    <groupId>biz.princeps</groupId>
    <artifactId>landlord-core</artifactId>
    <version>{version}</version>
</dependency>
```

# Wiki
There is a very in depth documentation about nearly everything in the
[wiki](https://github.com/LandlordPlugin/LandLord/wiki).

# Compilation

Gradle is the recommended way to build the project. Use `./gradlew shadowJar` in the main project directory to build the project.

# Contribution
[link](https://github.com/LandlordPlugin/LandLord/blob/master/CONTRIBUTING.md)