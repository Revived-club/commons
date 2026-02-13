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
    implementation(libs.gson)
    implementation(libs.bson)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
