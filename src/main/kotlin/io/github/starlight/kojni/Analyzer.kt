package io.github.starlight.kojni

object Analyzer {
  private val headerRegex: Regex =
      Regex("public (?:final|abstract)? (?:class|enum) ([a-z]+[.][A-Za-z0-9_]+) \\{")
  private val endRegex: Regex = Regex("}")

  fun analyzeFile(javapOutput: String): JniFile {
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
    val endInx = lines.indexOfLast { endRegex.matches(it) }
    lines = lines.subList(classHeaderLineIdx + 1, endInx)

    lines.forEach { line -> buildLineData(line, containerName)?.let { jniMethods.add(it) } }
    return JniFile(containerName, jniMethods)
  }

  private val methodRegex: Regex =
      Regex("([a-zA-z0-9.\\[\\]]+) ([a-zA-z_0-9]+)\\(([a-zA-z_0-9. \\[\\],]*)\\);")
  private fun buildLineData(line: String, fqcontainer: String): JniFunction? {
    if (!line.contains("native")) return null
    //        logger.debug(line)
    val (retVal, name, arglist) = methodRegex.find(line)?.destructured
        ?: throw Exception("Method Parse Error") // TODO: replace with failure method
    //        logger.debug("r{$retVal} n{$name} a{$arglist}")

    return JniFunction(
        fqcontainer = fqcontainer,
        retVal = retVal,
        methodName = name,
        args = arglist.split(','),
        isStatic = line.contains("static"))
  }
}
