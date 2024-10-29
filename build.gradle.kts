plugins {
    id("java")
    id("application")
}

group = "com.lichenaut"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.github.cdimascio:dotenv-java:3.0.2")
    implementation("net.dv8tion:JDA:5.1.2") {
        exclude(module = "opus-java")
    }
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "com.lichenaut.Main"
}