plugins {
    id("biz.princeps.library-conventions")
}

dependencies {
    api(project(":LandLord-api"))
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("io.github.bananapuncher714:nbteditor:7.16.1")
    implementation("de.eldoria:eldo-util:1.11.0-DEV")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("com.github.TownyAdvanced:Towny:0.96.1.11")
    compileOnly("net.luckperms:api:5.3")
    compileOnly("com.mojang:authlib:1.5.25")
}

description = "LandLord-core"
