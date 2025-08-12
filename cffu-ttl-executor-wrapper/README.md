# 🦝 `Cffu` executor wrapper provider SPI implementation for TTL

<p align="center">
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/cffu2-ttl-executor-wrapper/2.0.0-Alpha1/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu2-ttl-executor-wrapper?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://foldright.io/api-docs/cffu/"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu2-ttl-executor-wrapper?label=javadoc&logo=read-the-docs&logoColor=white" alt="Javadocs"></a>
</p>

`Cffu` executor wrapper provider(`ExecutorWrapperProvider`) SPI implementation for
[📌TransmittableThreadLocal(TTL)](https://github.com/alibaba/transmittable-thread-local).

Integrate [`TransmittableThreadLocal`](https://github.com/alibaba/transmittable-thread-local),
auto wrap cffu executor by `TtlExecutors.getTtlExecutor(...)`.

## 🍪 Dependency

This dependency should only be used at `Runtime`.

- For `Maven` projects:

  ```xml

  <dependency>
    <groupId>io.foldright</groupId>
    <artifactId>cffu2-ttl-executor-wrapper</artifactId>
    <scope>runtime</scope>
    <version>2.0.0-Alpha1</version>
  </dependency>
  ```
- For `Gradle` projects:

  Gradle Kotlin DSL
  ```groovy
  runtimeOnly("io.foldright:cffu2-ttl-executor-wrapper:2.0.0-Alpha1")
  ```
  Gradle Groovy DSL
  ```groovy
  runtimeOnly 'io.foldright:cffu2-ttl-executor-wrapper:2.0.0-Alpha1'
  ```

`cffu2-ttl-executor-wrapper` has published to maven central, find the latest version at
[central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu2-ttl-executor-wrapper/2.0.0-Alpha1/versions).
