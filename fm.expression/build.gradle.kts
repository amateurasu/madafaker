dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")

    testImplementation("org.openjdk.jmh:jmh-core:1.25.2")
    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.25.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.3.4.RELEASE")
}

