import de.chojo.PublishData

plugins {
    java
    id("de.chojo.publishdata") version "1.4.0"
}

group = "biz.princeps"
version = "5.0.0"

subprojects {
    apply {
        plugin<PublishData>()
    }
}
