plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://maven.enginehub.org/repo/") // Worldguard
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    api(project(":commons-core"))
    api(project(":commons-proto"))
    compileOnly(libs.papermc)
    compileOnly(libs.worldguard)
    implementation(libs.cloud)
    implementation(libs.cloudannotations)
    implementation(libs.fuzzywuzzy)
    implementation(libs.fastboard)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
