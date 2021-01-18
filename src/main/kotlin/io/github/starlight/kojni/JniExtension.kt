package io.github.starlight.kojni

import java.io.ByteArrayOutputStream
import java.io.File
import org.gradle.api.Project
import org.gradle.internal.jvm.Jvm

open class JniExtension(private val project: Project) {
  /**
   * Library name.
   *
   * Defaults to `"lib"`
   */
  var libName: String = "lib"

  /**
   * Root directory for headers.
   *
   * Defaults to `libName/src/main/headers`.
   */
  var headerDir: String = "$libName/src/main/headers"
  /**
   * Root directory for implementation files.
   *
   * Defaults to `libName/src/main/cpp`.
   */
  var implDir: String = "$libName/src/main/cpp"

  /**
   * The path to the root directory of compiled `.class` files. Relative to the gradle build output
   * dir (`/build/`).
   *
   * Defaults to `classes/kotlin/main/`.
   */
  var classesPath: String = "classes/kotlin/main"

  /**
   * Set whether to generate stub `.cpp` files. Even if this is set to `true`, KoJni will **never**
   * overwrite an existing implementation file.
   *
   * Defaults to `false`.
   */
  var generateImpl: Boolean = false

  /**
   * Path to JAVA_HOME folder.
   *
   * Defaults to the home folder of the current JVM.
   */
  var javaHome: File = Jvm.current().javaHome

  /**
   * Set whether to include generation of private JNI functions.
   *
   * Defaults to `true`.
   */
  var includePrivate = true

  internal fun classfiles(): List<File> {
    val ret =
        project
            .buildDir
            .resolve(classesPath.replace('/', File.separatorChar))
            .walk()
            .toSet()
            .filter { it.extension == "class" }
    //          .filter { "native" in it.readText() }
    project.logger.info(ret.toString())
    return ret
  }

  private val javap = javaHome.resolve("bin").resolve("javap")

  internal fun runJavaP(file: File): String {
    ByteArrayOutputStream().use { outputStream ->
      project.exec {
        it.executable = javap.absolutePath
        it.args = listOf(if (includePrivate) "-private" else "", file.absolutePath)
        it.standardOutput = outputStream
      }
      val output = outputStream.toString()
      project.logger.info(output)
      return output
    }
  }
}

// sealed class Language(val lang: String) {
//  object Kotlin : Language("kotlin")
//  object Java : Language("java")
//  object Scala : Language("scala")
//  object Groovy : Language("groovy")
//  class Other(lang: String) : Language(lang)
// }
