# Code of Conduct

This document defines the coding standards, architectural principles, and best practices for contributions to this library.

## 1. Data Objects

* **Records First:** All data objects should be implemented as `record`s unless explicitly exempted.
* **POJO Exception:** Plain Old Java Objects (POJOs) are permitted exclusively for InfluxDB-related data objects.
* **Protobuf Usage:**

  * Use Protobuf for message definitions whenever possible.
  * All Protobuf objects (besides enum) **must be documented**, including field descriptions and usage notes.

## 2. Immutability and Variables

* **Final Variables:** All variables should be declared `final` unless mutability is explicitly required and justified.
* **Immutable Design:** Design classes and objects to be immutable by default wherever possible.

## 3. Abstraction Layers

* **Double Abstraction Principle:**

  * Core objects in `commons-core` must serve as abstract bases.
  * Implementations in platform-specific modules (e.g., Bukkit, Velocity) should extend these core objects.
  * Example:

    ```java
    // commons-core
    public abstract class ArenaManager { ... }

    // commons-bukkit
    public class BukkitArenaManager extends ArenaManager { ... }
    ```

* This ensures platform-agnostic core logic and clean separation of concerns.

## 4. Documentation

* **Protobuf:** All Protobuf message objects and fields must have descriptive comments.

## 5. Database Operations

* **Composito API:** All database interactions must use the Composito database API. Direct database queries outside of Composito are strictly prohibited.
* **Consistency:** Use Protobuf objects for persistence and messaging where feasible, to ensure consistency across services.

## 6. General Principles

* **Clean Architecture:** Follow SOLID principles; avoid tight coupling and ensure clear separations between modules.
* **Consistency:** Enforce uniform naming conventions, indentation, and coding style across all modules.
