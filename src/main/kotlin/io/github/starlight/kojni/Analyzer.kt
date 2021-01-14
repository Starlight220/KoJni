package io.github.starlight.kojni

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger

class Analyzer(task: DefaultTask) {
  private val logger: Logger = task.logger
  private val headerRegex: Regex =
      Regex("public (?:final|abstract)? (?:class|enum) ([a-z]+[.][A-Za-z0-9_]+) \\{")
  private val endRegex: Regex = Regex("}")

  fun analyzeFile(javapOutput: String): JniFile {
    logger.info("\n===========Analyzer::analyzeFile=======")
    logger.info(javapOutput)
    val jniMethods = HashSet<JniFunction>(javapOutput.length - 4)
    var lines = javapOutput.split(System.lineSeparator())
    var containerName: String = ""
    val classHeaderLineIdx =
        lines.indexOfFirst { line ->
          headerRegex.find(line)?.destructured?.let {
            containerName = it.component1().replace(".", "_")
            true
          }
              ?: false
        }
    if (classHeaderLineIdx == -1) {
      return JniFile("-1", emptySet()) // fail("No class declaration found")
    }
    logger.info(containerName)
    logger.info(lines.toString())
    val endInx = lines.indexOfLast { endRegex.matches(it) }

    lines.forEach { line -> buildLineData(line, containerName)?.let { jniMethods.add(it) } }
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
        args = arglist.split(','),
        isStatic = line.contains("static"))
  }
}
