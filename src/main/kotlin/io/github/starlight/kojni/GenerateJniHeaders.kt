package io.github.starlight.kojni

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType

open class GenerateJniHeaders() : DefaultTask() {
  private val jniExt: JniExtension = project.extensions.getByType()

  @OutputDirectory @InputDirectory var jniHeaders: File = project.file(jniExt.headerDir)
  @Input var generateImpl: Boolean = false
  @Input val implDir: File = project.file(jniExt.implDir)

  @TaskAction
  fun generate() {
    val analyzer = Analyzer(this)
    val javapOutput = jniExt.classfiles().info().map { file -> jniExt.runJavaP(file) }.info()

    val jniFiles: List<JniFile> =
        javapOutput.mapNotNull { output -> analyzer.analyzeFile(output) }.info()

    jniFiles.forEach { jnifile ->
      val (filename, content) = jnifile // destruct
      writeFile(filename, content)
      logger.warn(updatedWarn(filename))
      logger.info("$generateImpl |||| ${!implDir.resolve("${filename}.cpp)").exists()}")
      if (generateImpl && !implDir.resolve("${filename}.cpp").exists()) {
        writeFile(dir = implDir, filename = filename, suffix = "cpp", content = jnifile.impl())
      }
    }
  }

  private fun writeFile(
      filename: String,
      content: String,
      suffix: String = "h",
      dir: File = jniHeaders
  ) {
    logger.info("writing file ${filename}.${suffix}")
    if (content.isBlank()) return
    val file = dir.resolve("$filename.$suffix")
    if (file.exists()) {
      logger.warn("${file.path} exists")
    }
    file.parentFile.mkdirs()
    if (!file.createNewFile()) {
      logger.error("::Wasn't able to create file ${file.path}")
    }
    file.writeText(content)
    logger.info("Written $filename to ${file.path}")
  }

  private fun <R> List<R>.info() = apply { logger.info(this.toString()) }

  private fun updatedWarn(filename: String) =
      """
    The header linked to ${filename}.cpp has been updated, so the implementation might be out-of-date.
    Please make sure that all functions are implemented with the correct signature.
  """.trimIndent()

  private fun createdWarn(filename: String) =
      """
    An implementation file ${filename}.cpp has been generated for the matching generated header.
    To avoid compilation/linkage errors, implement the generated function stubs.
  """.trimIndent()
}
