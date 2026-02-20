plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    api(project(":commons-core"))
    compileOnly(libs.velocity)
    api(project(":commons-proto"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

