import org.gradle.internal.jvm.Jvm
import org.gradle.internal.os.OperatingSystem

fun nexus(host: String, port: Int = 8000) = uri("http://$host:$port/repository/maven/")
repositories {
    // mavenCentral()
    maven { url = nexus("172.16.28.46") }
}

val os: OperatingSystem = OperatingSystem.current()
println("Building '${rootProject.name}' on ${os.name} ver ${os.version} with ${Jvm.current()}...")

plugins {
    java
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

allprojects {
    group = "com.viettel.ems"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        // mavenCentral()
        // maven { url = uri("https://packages.confluent.io/maven/") }
        maven { url = nexus("172.16.28.46") }
    }

    dependencies {
        "org.projectlombok:lombok:1.18.12".let {
            compileOnly(it)
            annotationProcessor(it)
            testCompileOnly(it)
            testAnnotationProcessor(it)
        }
    }

    tasks.register<Copy>("copy-libs") {
        doFirst {
            println("Copy dependencies of module ${"%-15s".format(project.name)} into $rootDir/libs/${project.name}...")
        }

        from(configurations.default) {
            into("$rootDir/libs/${project.name}")
        }

        doLast {
            println("Done copying dependencies of module ${project.name}!")
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}
