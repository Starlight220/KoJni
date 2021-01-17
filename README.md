# KoJni [WIP]
A Gradle Plugin for generating JNI header files.

### The problem
Since JDK 8+, `javah` (a tool for generating JNI header files) isn't included. The replacement is `javac -h`. However, `javac` runs only on Java source code; while `javah` would work on `.class` files.
As a result from the change, other JVM languages (such as Kotlin) were left without a solution for generating JNI headers. 

### The Solution
KoJni is a gradle plugin that handles JNI header generation for you, via the `generateJni` task. Configurations are still TBD. 

### Current status
KoJni is currently in the proof-of-concept stage. 
