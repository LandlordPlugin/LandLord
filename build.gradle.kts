import de.chojo.PublishData

plugins {
    java
    id("de.chojo.publishdata") version "1.0.4"
}

group = "biz.princeps"
version = "4.363"

subprojects {
    apply {
        plugin<PublishData>()
    }
}
