plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    implementation(libs.jetbrainsannotations)
    implementation(libs.kubernetes)
    implementation(libs.bson)
    implementation(libs.gson)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
