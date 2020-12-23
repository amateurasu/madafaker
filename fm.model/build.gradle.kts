import com.google.protobuf.gradle.*

plugins {
    id("com.google.protobuf") version "0.8.13"
}

dependencies {
    implementation(project(":fm.expression"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:4.10.0")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:2.3.4.RELEASE")

    implementation("io.grpc:grpc-stub:1.33.1")
    implementation("io.grpc:grpc-protobuf:1.33.1")
    implementation("com.google.protobuf:protobuf-java:3.14.0")
    implementation("com.google.protobuf:protobuf-java-util:3.14.0")
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn("generateProto")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.13.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { it.plugins { id("grpc") } }
    }
}
