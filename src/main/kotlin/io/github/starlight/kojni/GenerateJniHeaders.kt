package io.github.starlight.kojni

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType

open class GenerateJniHeaders() : DefaultTask() {
  private val jniExt: JniExtension = project.extensions.getByType()

  //  @OutputDirectory val jniHeaders: File? = null

  @TaskAction
  fun generate() {
    jniExt.run {
      classfiles()[0].let { runJavaP(it) }.let { Analyzer.analyzeFile(it) }.buildFile().let {
        logger.warn(it)
      }
    }
  }
}
//
// private fun fail(reason: String): Nothing {
//    logger.error(reason)
//    throw TaskExecutionException(this, Exception(reason)) // TODO: create error class
// }
