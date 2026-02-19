plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":commons-core"))
    implementation(libs.jetbrainsannotations)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}