[![Plugin Portal](https://img.shields.io/maven-metadata/v?label=Gradle%20Plugin%20Portal&color=blue&metadataUrl=https://plugins.gradle.org/m2/io/github/starlight/KoJni/maven-metadata.xml)](https://plugins.gradle.org/plugin/io.github.starlight.KoJni)
# KoJni

A Gradle Plugin for generating JNI header files.

### The problem

Since JDK 8+, `javah` (a tool for generating JNI header files) isn't included. The replacement
is `javac -h`. However, `javac` runs only on Java source code; while `javah` would work on `.class`
files. As a result from the change, other JVM languages (such as Kotlin) were left without a
solution for generating JNI headers.

### The Solution

KoJni is a gradle plugin that handles JNI header generation for you, via the `generateJni` task.

### Current status

KoJni 1.0.0 is stable. See the [roadmap](https://github.com/Starlight220/KoJni/projects/1) for more
info about current capabilities and future versions.

## Usage / Configuration

KoJni is intended to work with the
[new](https://docs.gradle.org/current/userguide/building_cpp_projects.html) native plugins, with the
native lib as a subproject (see [this issue](https://github.com/gradle/gradle-native/issues/216)).

Example file structure:
```
.
|   build.gradle(.kts)
|   settings.gradle(.kts)
+---example
|   |   build.gradle(.kts)
|   \---src
|       \---main
|           +---cpp
|           \---headers
\---src
    \---main
        \---kotlin
```

Root `build.gradle.kts` for this example:
```kotlin
plugins {
  kotlin("jvm") version "1.4.21" // kotlin plugin
  id("io.github.starlight.KoJni") version "1.0.0"
}
repositories {
  mavenCentral()
  jcenter()
}
jni {
  libName = "example" // name of jni lib folder 
  tasks.generateJni.configure {
    dependsOn("compileKotlin") // depends on JVM compilation
  }
}
```
