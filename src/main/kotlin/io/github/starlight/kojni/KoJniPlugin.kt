package io.github.starlight.kojni

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class KoJniPlugin : Plugin<Project> {
  private lateinit var project: Project
  private lateinit var jniExt: JniExtension
  override fun apply(target: Project) {
    project = target
    jniExt = target.extensions.create<JniExtension>("jni", target)

    target.tasks.register<GenerateJniHeaders>("generateJni") { generateImpl = jniExt.generateImpl }
  }
  //
  //    fun fail(message: String): Nothing {
  //        project.logger.error(message)
  //        throw Exception(message)
  //    }
}
