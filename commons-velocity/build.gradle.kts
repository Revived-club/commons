plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    implementation(project(":commons-core"))
    compileOnly(libs.velocity)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
