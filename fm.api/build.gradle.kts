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
        File("$projectDir/build/libs/", "run").writeText("docker build -t ems/${project.name}:${project.version} .")
    }
}

tasks.register("build-profile") {
    dependsOn("bootJar")
}

tasks.getByName<BootRun>("bootRun") {
    main = "com.viettel.ems.API"
}
