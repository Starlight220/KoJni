package io.github.starlight.kojni

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger

class Analyzer(task: DefaultTask) {
  private val logger: Logger = task.logger
  private val headerRegex: Regex =
      Regex("public (?:final|abstract)? (?:class|enum) ([a-z]+[.][A-Za-z0-9_]+) \\{")
  private val endRegex: Regex = Regex("}")

  fun analyzeFile(javapOutput: String): JniFile? {
    logger.info("\n===========Analyzer::analyzeFile=======")
    logger.info(javapOutput)
    if ("native" !in javapOutput) return null
    val jniMethods = HashSet<JniFunction>(javapOutput.length - 4)
    var lines = javapOutput.split(System.lineSeparator())
    var containerName: String = ""
    lines
        .indexOfFirst { line ->
          headerRegex.find(line)?.destructured?.let {
            containerName = it.component1()
            true
          }
              ?: false
        }
        .takeUnless { it == -1 }
        ?: throw Exception("invalid line")

    logger.info(containerName)
    logger.info(lines.toString())

    lines.forEach { line ->
      buildLineData(line, containerName.replace(".", "_"))?.let { jniMethods.add(it) }
    }
    logger.info(jniMethods.size.toString())
    logger.info("------------\n")
    return JniFile(containerName, jniMethods)
  }

  private val methodRegex: Regex =
      Regex("([a-zA-z0-9.\\[\\]]+) ([a-zA-z_0-9]+)\\(([a-zA-z_0-9. \\[\\],]*)\\);")
  private fun buildLineData(line: String, fqcontainer: String): JniFunction? {
    if (!line.contains("native")) return null
    logger.info(line)
    val (retVal, name, arglist) = methodRegex.find(line)?.destructured
        ?: throw Exception("Method Parse Error") // TODO: replace with failure method
    logger.info("r{$retVal} n{$name} a{$arglist}")

    return JniFunction(
        fqcontainer = fqcontainer,
        retVal = retVal,
        methodName = name,
        args = arglist.takeUnless { arglist.isBlank() }?.split(','),
        isStatic = line.contains("static"))
  }
}
