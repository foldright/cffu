# Guava ListenableFuture Integration with CompletableFuture

<p align="center">
<a href="https://central.sonatype.com/artifact/io.foldright/cffu-listenable-future/0.9.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu-listenable-future?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://foldright.io/api-docs/cffu-listenable-future/"><img src="https://img.shields.io/github/release/foldright/cffu?label=dokka&color=339933&logo=kotlin&logoColor=white" alt="dokka"></a>
</p>

- Sources:
  - [`ListenableFutureUtils.java`](src/main/java/io/foldright/cffu/lf/ListenableFutureUtils.java)
  - [`ListenableFutureExtensions.kt`](src/main/java/io/foldright/cffu/lf/kotlin/ListenableFutureExtensions.kt)
- Dependency:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-listenable-future</artifactId>
      <version>1.0.0-Alpha33</version>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    implementation("io.foldright:cffu-listenable-future:1.0.0-Alpha33")
    ```
    Gradle Groovy DSL
    ```groovy
    implementation 'io.foldright:cffu-listenable-future:1.0.0-Alpha33'
    ```
