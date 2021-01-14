package io.github.starlight.kojni

import java.io.ByteArrayOutputStream
import java.io.File
import org.gradle.api.Project
import org.gradle.internal.jvm.Jvm

open class JniExtension(private val project: Project) {
  var libName: String = "lib"
  var headerDir: String = "$libName/src/main/headers"
  var implDir: String = "lib/src/main/cpp"
  var lang: Language = Language.Kotlin
  var modules: MutableCollection<String> = mutableSetOf("main")
  var classesPath: String = "classes/kotlin/main/"
  var generateImpl: Boolean = true
  var javaHome: File = Jvm.current().javaHome

  internal fun classfiles() =
      project.buildDir.resolve(classesPath.replace('/', File.separatorChar)).walk().toSet().filter {
        it.extension == "class"
      }

  private val javap = javaHome.resolve("bin").resolve("javap")

  internal fun runJavaP(file: File): String {
    ByteArrayOutputStream().use { outputStream ->
      project.exec {
        it.executable = javap.absolutePath
        it.args = listOf("-private", file.absolutePath)
        it.standardOutput = outputStream
      }
      val output = outputStream.toString()
      project.logger.debug(output)
      return output
    }
  }
}

sealed class Language(val lang: String) {
  object Kotlin : Language("kotlin")
  object Java : Language("java")
  object Scala : Language("scala")
  object Groovy : Language("groovy")
  class Other(lang: String) : Language(lang)
}
