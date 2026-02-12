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
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
