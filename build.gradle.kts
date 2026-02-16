
plugins {
    `maven-publish`
    java
}

group = "club.revived.commons"
version = "0.1.17"

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = "commons"
            version = project.version.toString()
        }
    }

    repositories {
        maven {
            name = "revived"
            url = uri("https://mvn.revived.club/releases")
            credentials {
                username = project.findProperty("repoUser")?.toString() ?: ""
                password = project.findProperty("repoPass")?.toString() ?: ""
            }
        }
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = "club.revived.commons"
    version = rootProject.version.toString()

    java {
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = project.name
            }
        }

        repositories {
            maven {
                name = "revived"
                url = uri("https://mvn.revived.club/releases")
                credentials {
                    username = rootProject.findProperty("repoUser")?.toString() ?: ""
                    password = rootProject.findProperty("repoPass")?.toString() ?: ""
                }
            }
        }
    }
}
