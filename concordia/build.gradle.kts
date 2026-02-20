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
    implementation(libs.lettuce)
    api(project(":commons-proto"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
