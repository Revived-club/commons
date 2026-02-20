plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jetbrainsannotations)
    implementation(libs.nats)
    api(project(":commons-proto"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

