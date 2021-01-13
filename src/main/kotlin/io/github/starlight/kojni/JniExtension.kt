package io.github.starlight.kojni

import org.apache.log4j.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.getByType
import java.io.File

open class JniExtension(private val project: Project) {
    var headerDir: String = "lib/src/main/headers"
    var implDir: String = "lib/src/main/cpp"
    var generateImpl: Boolean = true
}

class GenerateJniHeaders() : DefaultTask() {
    private val logger = Logger.getLogger("KJ_GEN")
    private val jniExt: JniExtension = project.extensions.getByType()

    @OutputDirectory
    val jniHeaders: File? = null

    @TaskAction
    fun generate() {

    }

    private val headerRegex: Regex =
        Regex("public (?:final|abstract)? (?:class|enum) ([a-z]+[.][A-Za-z0-9_]+) \\{")
    private val methodRegex: Regex =
        Regex("([a-zA-z0-9.\\[\\]]+) ([a-zA-z_0-9]+)\\(([a-zA-z_0-9. \\[\\],]*)\\);")
    private val endRegex: Regex = Regex("}")
    fun analyzeFile(javapOutput: String): Set<JniFunction> {
        val jniMethods = HashSet<JniFunction>(javapOutput.length - 4)
        var lines = javapOutput.split(System.lineSeparator())
        var containerName: String = ""
        val classHeaderLineIdx = lines.indexOfFirst { line ->
            headerRegex.find(line)?.destructured?.let {
                containerName = it.component1()
                true
            } ?: false
        }
        if (classHeaderLineIdx == -1) {
            fail("No class declaration found")
        }
        val endInx = lines.indexOfLast { endRegex.matches(it) }
        lines = lines.subList(classHeaderLineIdx + 1, endInx)

        lines.forEach { line ->
            buildLineData(line, containerName)?.let { jniMethods.add(it) }
        }
        return jniMethods
    }

    private fun fail(reason: String): Nothing {
        logger.error(reason)
        throw TaskExecutionException(this, Exception(reason)) // TODO: create error class
    }

    private fun buildLineData(line: String, fqcontainer: String): JniFunction? {
        if (!line.contains("native")) return null
        logger.debug(line)
        val (retVal, name, arglist) = methodRegex.find(line)?.destructured
            ?: fail("Method Parse Error")
        logger.debug("r{$retVal} n{$name} a{$arglist}")

        return JniFunction(
            fqcontainer = fqcontainer,
            retVal = retVal,
            name = name,
            args = arglist.split(','),
            static = line.contains("static")
        )
    }
}

