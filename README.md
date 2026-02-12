# Revived Commons

A utility library providing common helpers, abstractions, and reusable code for Minecraft server development.

## Overview

Revived Commons is a lightweight Java library offering shared utilities and helper classes used across Minecraft plugins and server infrastructure. It’s designed to simplify development by providing reusable solutions for common tasks.

## Architecture

```
commons/
├── commons-core/           # Core utilities and shared code
├── commons-bukkit/         # Bukkit/Spigot/Paper specific helpers
└── commons-velocity/       # Velocity proxy specific helpers
```

## Installation

### Maven

```xml
<repositories>
    <repository>
        <id>revived-releases</id>
        <url>https://mvn.revived.club/releases</url>
    </repository>
</repositories>

<dependencies>
    <!-- Core library -->
    <dependency>
        <groupId>club.revived.commons</groupId>
        <artifactId>commons-core</artifactId>
        <version>VERSION</version>
    </dependency>
    
    <!-- For Bukkit/Spigot/Paper servers -->
    <dependency>
        <groupId>club.revived.commons</groupId>
        <artifactId>commons-bukkit</artifactId>
        <version>VERSION</version>
    </dependency>
    
    <!-- For Velocity proxies -->
    <dependency>
        <groupId>club.revived.commons</groupId>
        <artifactId>commons-velocity</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

### Gradle (Kotlin DSL)

```kotlin
maven {
    name = "revived-releases"
    url = uri("https://mvn.revived.club/releases")
}

dependencies {
    implementation("club.revived.commons:commons-core:VERSION")
    implementation("club.revived.commons:commons-bukkit:VERSION")
    implementation("club.revived.commons:commons-velocity:VERSION")
}
```

## Building from Source

Requirements:

* Java 25 or higher
* Gradle 9.2 or higher

```bash
# Clone the repository
git clone https://github.com/Revived-club/commons.git
cd commons

# Build all modules
./gradlew build
```

## Development Status

⚠️ **Work in Progress** - This library is actively maintained. APIs may change until the first stable release.

## Requirements

* **Java**: 25 or higher
* **Bukkit/Spigot/Paper**: 1.21.10 or higher
* **Velocity**: 3.0.0 or higher

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the terms specified in the repository. See the LICENSE file for details.
