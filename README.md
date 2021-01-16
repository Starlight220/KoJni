# KoJni
A Gradle Plugin for generating JNI header files.

### The problem
Since JDK 8+, `javah` (a tool for generating JNI header files) isn't included. The replacement is `javac -h`. However, `javac` runs only on Java source code; while `javah` would work on `.class` files.
As a result from the change, other JVM languages (such as Kotlin) were left without a solution for generating JNI headers. 
