plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.1.10"
    application
    id("com.google.protobuf") version "0.9.4"  // Add the Protobuf plugin
}

group = "dev.lochert.agrok"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()
}

application {
    mainClass = "dev.lochert.ds.blockchain.http.server.DockerInitKt"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.0"  // Set the protoc version
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("kotlin") {
                    option("lite")  // Generates Kotlin code
                }
            }
        }
    }
}

sourceSets {
    main {
        proto {
            srcDir("src/main/resources/") // Your .proto files location
        }
        java {
            srcDir("build/generated/source/proto/main/kotlin") // Where generated Kotlin files will go
        }
    }
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