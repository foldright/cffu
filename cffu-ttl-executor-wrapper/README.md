# 🦝 cffu executor wrapper provider SPI implementation for TTL

Integrate [`TransmittableThreadLocal`](https://github.com/alibaba/transmittable-thread-local),
auto wrap cffu executor by `TtlExecutors.getTtlExecutor(...)`.

## 🍪 Dependency

This dependency should only need at `Runtime`.

For `Maven` projects:

```xml

<dependency>
  <groupId>io.foldright</groupId>
  <artifactId>cffu-ttl-executor-wrapper</artifactId>
  <scope>runtime</scope>
  <version>0.9.4</version>
</dependency>
```

For `Gradle` projects:

```groovy
// Gradle Kotlin DSL
runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:0.9.4")

// Gradle Groovy DSL
runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:0.9.4'
```

`cffu-ttl-executor-wrapper` has published to maven central, find the latest version at
[central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu-ttl-executor-wrapper/0.9.4/versions).
