plugins {
    `java-library`
    alias(libs.plugins.shadow)
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
    api(project(":concordia"))
    api(project(":commons-proto"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
