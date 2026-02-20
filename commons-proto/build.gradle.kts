plugins {
    `java-library`
    alias(libs.plugins.protobuf)
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.protobuf.java)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.3"
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/main/proto")
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
