import de.chojo.PublishData

plugins {
    java
    id("de.chojo.publishdata") version "1.0.4"
}

subprojects {
    apply {
        plugin<PublishData>()
    }
}
