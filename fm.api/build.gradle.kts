import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

val version = mapOf("boot" to "2.3.4.RELEASE", "cloud" to "2.2.3.RELEASE", "kafka" to "2.6.0")

fun spring(project: String, module: String = ""): String {
    return "org.springframework.$project:spring-$project$module:${version[project]}"
}

dependencies {
    implementation(project(":fm.model"))
    implementation(project(":fm.expression"))

    implementation(spring("boot", "-starter"))
    implementation(spring("boot", "-starter-data-jdbc"))
    implementation(spring("boot", "-starter-data-rest"))
    implementation(spring("boot", "-starter-actuator"))
    implementation(spring("boot", "-starter-webflux"))
    implementation(spring("boot", "-starter-tomcat"))
    implementation(spring("cloud", "-starter"))

    implementation("mysql:mysql-connector-java:8.0.19")
    implementation("org.apache.commons:commons-text:1.8")

    testImplementation(spring("boot", "-starter-test"))
}


tasks.getByName<BootJar>("bootJar") {
    mainClassName = "com.viettel.ems.API"

    doLast {
        val folder = File("$projectDir/build/libs/").apply { mkdirs() }
        val name = project.name
        val version = project.version

        File(folder, "Dockerfile").writeText(
            """
            FROM openjdk:11.0.7-jre
            LABEL maintainer="duclm22@viettel.com.vn"
            COPY ./$name-$version.jar /opt/ems/
            RUN echo "Asia/Ho_Chi_Minh" > /etc/timezone
            CMD ["java", "-jar", "-Dspring.profiles.active=production", "/opt/ems/$name-$version.jar"]
            """.trimIndent())

        File(folder, "docker.sh").writeText("docker build -t ems/${name}:$version .")
    }
}

tasks.getByName<BootRun>("bootRun") {
    main = "com.viettel.ems.API"
}
