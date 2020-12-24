rootProject.name = "fm"

pluginManagement {
    repositories {
        maven("http://172.16.28.46:8000/repository/maven/")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.google.protobuf") {
                useModule("com.google.protobuf:protobuf-gradle-plugin:${requested.version}")
            }
        }
    }
}

include(":fm.api")
include(":fm.gateway")
include(":fm.processor")
include(":fm.expression")
include(":fm.model")
