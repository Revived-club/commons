plugins {
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.protobuf)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    implementation(libs.jetbrainsannotations)
    implementation(libs.kubernetes)
    api(libs.gson)
    implementation(libs.mongo)
    implementation(libs.jedis)
    implementation(libs.influxdb)
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