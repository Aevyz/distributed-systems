plugins {
    kotlin("jvm") version "2.1.20"
    id("com.google.protobuf") version "0.9.4"  // Add the Protobuf plugin
}

group = "dev.lochert.distributedsystems"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
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
            srcDir("src/main/resources/protobuf") // Your .proto files location
        }
        java {
            srcDir("build/generated/source/proto/main/kotlin") // Where generated Kotlin files will go
        }
    }
}