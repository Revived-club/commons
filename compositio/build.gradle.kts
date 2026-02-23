plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jetbrainsannotations)
    implementation(libs.mongo)
    implementation(libs.influxdb)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
