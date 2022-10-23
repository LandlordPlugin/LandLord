import de.chojo.PublishData

plugins {
    java
    id("de.chojo.publishdata") version "1.0.9"
}

group = "biz.princeps"
version = "5.0.0"

subprojects {
    apply {
        plugin<PublishData>()
    }
}
