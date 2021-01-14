package io.github.starlight.kojni

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class KoJniPlugin : Plugin<Project> {
  private lateinit var project: Project
  override fun apply(target: Project) {
    project = target
    target.extensions.create<JniExtension>("jni", target)

    target.tasks.register<GenerateJniHeaders>("generateJni")
  }
  //
  //    fun fail(message: String): Nothing {
  //        project.logger.error(message)
  //        throw Exception(message)
  //    }
}
