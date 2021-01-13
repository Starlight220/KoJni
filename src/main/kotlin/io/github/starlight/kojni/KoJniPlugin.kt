package io.github.starlight.kojni
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class KoJniPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create<JniExtension>("jni")
    }
}