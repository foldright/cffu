# 🦝 `Cffu` executor wrapper provider SPI implementation for TTL

`Cffu` executor wrapper provider(`ExecutorWrapperProvider`) SPI implementation for
[📌TransmittableThreadLocal(TTL)](https://github.com/alibaba/transmittable-thread-local).

Integrate [`TransmittableThreadLocal`](https://github.com/alibaba/transmittable-thread-local),
auto wrap cffu executor by `TtlExecutors.getTtlExecutor(...)`.

## 🍪 Dependency

This dependency should only be used at `Runtime`.

For `Maven` projects:

```xml

<dependency>
  <groupId>io.foldright</groupId>
  <artifactId>cffu-ttl-executor-wrapper</artifactId>
  <scope>runtime</scope>
  <version>1.0.0-Alpha22</version>
</dependency>
```

For `Gradle` projects:

```groovy
// Gradle Kotlin DSL
runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:1.0.0-Alpha22")
```

```groovy
// Gradle Groovy DSL
runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:1.0.0-Alpha22'
```

`cffu-ttl-executor-wrapper` has published to maven central, find the latest version at
[central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu-ttl-executor-wrapper/0.9.4/versions).
