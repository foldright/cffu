# 🦝 `Cffu` kotlin support 🍩

<p align="center">
<a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.6+-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/cffu-kotlin/0.9.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu-kotlin?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://foldright.io/api-docs/cffu-kotlin/"><img src="https://img.shields.io/github/release/foldright/cffu?label=dokka&color=339933&logo=kotlin&logoColor=white" alt="dokka"></a>
</p>

This module provides the extension methods to leverage Kotlin language.

- `Kotlin API` documentation(dokka): https://foldright.io/api-docs/cffu-kotlin/
- `Kotlin extensions`:
  - [`CffuExtensions.kt`](src/main/java/io/foldright/cffu/kotlin/CffuExtensions.kt)
  - [`CompletableFutureExtensions.kt`](src/main/java/io/foldright/cffu/kotlin/CompletableFutureExtensions.kt)
- Dependency:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-kotlin</artifactId>
      <version>1.0.0-Alpha30</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu-kotlin:1.0.0-Alpha30")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu-kotlin:1.0.0-Alpha30'
    ```
