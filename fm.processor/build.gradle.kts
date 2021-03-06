import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "com.viettel.ems.Processor"
}

tasks.getByName<BootRun>("bootRun") {
    main = "com.viettel.ems.Processor"
}

val version = mapOf("boot" to "2.3.4.RELEASE", "cloud" to "2.2.3.RELEASE", "kafka" to "2.6.0")

fun spring(project: String, module: String = ""): String {
    return "org.springframework.$project:spring-$project$module:${version[project]}"
}

dependencies {
    implementation(project(":fm.model"))
    implementation(project(":fm.expression"))

    implementation(spring("boot", "-starter"))
    implementation(spring("boot", "-starter-jdbc"))
    implementation(spring("boot", "-starter-webflux"))
    implementation(spring("boot", "-starter-actuator"))
    implementation(spring("cloud", "-starter"))
    implementation(spring("kafka"))

    implementation("io.grpc:grpc-stub:1.33.1")
    implementation("io.grpc:grpc-protobuf:1.33.1")
    implementation("com.google.protobuf:protobuf-java:3.13.0")
    implementation("io.confluent:kafka-protobuf-serializer:6.0.0")

    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:4.10.0")
    implementation("mysql:mysql-connector-java:8.0.19")

    testImplementation(spring("boot", "-starter-test"))
}
