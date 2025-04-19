# Guava ListenableFuture Integration with CompletableFuture

<p align="center">
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/cffu-listenable-future/1.0.0-Alpha/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu-listenable-future?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://foldright.io/api-docs/cffu/"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu-listenable-future?label=javadoc&logo=read-the-docs&logoColor=white" alt="Javadocs"></a>
</p>

> [!IMPORTANT]
> 🚧 This module is currently in Alpha status and NOT stable.

- Sources:
  - [`ListenableFutureUtils.java`](src/main/java/io/foldright/cffu/lf/ListenableFutureUtils.java)
  - [`ListenableFutureExtensions.kt`](src/main/java/io/foldright/cffu/lf/kotlin/ListenableFutureExtensions.kt)
- Dependency:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-listenable-future</artifactId>
      <version>1.1.3-Alpha</version>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    implementation("io.foldright:cffu-listenable-future:1.1.3-Alpha")
    ```
    Gradle Groovy DSL
    ```groovy
    implementation 'io.foldright:cffu-listenable-future:1.1.3-Alpha'
    ```
