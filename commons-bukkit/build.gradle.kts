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
    api(project(":commons-core"))
    compileOnly(libs.papermc)
    compileOnly(libs.worldguard)
    implementation(libs.cloud)
    implementation(libs.cloudannotations)
    implementation(libs.fuzzywuzzy)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
