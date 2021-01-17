package io.github.starlight.kojni

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getByType
import java.io.File

open class GenerateJniHeaders() : DefaultTask() {
  private val jniExt: JniExtension = project.extensions.getByType()

  @OutputDirectory @InputDirectory
  val jniHeaders: File = project.file(jniExt.headerDir)

  @TaskAction
  fun generate() {
    val analyzer = Analyzer(this)
    val javapOutput = jniExt.classfiles()
      .info()
      .map { file -> jniExt.runJavaP(file) }
      .info()

    val jniFiles: List<JniFile> = javapOutput
      .map { output -> analyzer.analyzeFile(output) }
      .info()

    jniFiles.forEach { (filename, filecontent) ->
      val file = jniHeaders.resolve("$filename.h")
      if (file.exists()) {
        logger.warn("${file.path} exists")
      }
      file.mkdirs()
      if (!file.canWrite()) {
        logger.error("Access Denied to file ${file.path}")
      }
      file.writeText(filecontent)
      logger.info("Written $filename to ${file.path}")
    }
  }

  private fun <R> List<R>.info() = apply {
    logger.info(this.toString())
  }
}

