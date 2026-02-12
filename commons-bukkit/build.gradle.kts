plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://maven.enginehub.org/repo/") // Worldguard
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    implementation(project(":commons-core"))
    compileOnly(libs.papermc)
    compileOnly(libs.worldguard)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
