plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.1.10"
}

group = "dev.lochert.agrok"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.openjfx:javafx-controls:17.0.2")
    implementation("org.openjfx:javafx-graphics:17.0.2")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.jgrapht:jgrapht-io:1.5.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}