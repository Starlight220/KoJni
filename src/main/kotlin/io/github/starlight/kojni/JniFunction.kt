package io.github.starlight.kojni

class JniFunction(
    val fqcontainer: String,
    val isStatic: Boolean,
    val methodName: String,
    val retVal: String,
    args: List<String>
) {
  val c_args: String by lazy {
    val builder = StringBuilder()
    args.forEach { builder.append(", ").append(mapTypeToC(it)) }
    return@lazy builder.toString()
  }
  val d_args: String by lazy {
    val builder = StringBuilder()
    args.forEach { builder.append(mapTypeToD(it)) }
    return@lazy builder.toString()
  }

  fun buildFunction(): String {
    return """
        /*
         * Class:     ${fqcontainer}
         * Method:    ${methodName}
         * Signature: (${d_args})${mapTypeToD(retVal)}
         */
        JNIEXPORT ${mapTypeToC(retVal)} JNICALL Java_${fqcontainer}_${methodName}
          (JNIEnv *, ${if (isStatic) "jclass" else "jobject"}${c_args});
        
    """
  }
}

val javaPrimitives = setOf("boolean", "byte", "char", "short", "int", "long", "float", "double")

internal fun mapTypeToC(type: String, recursed: Boolean = false): String {
  return when (type.trim()) {
    in javaPrimitives -> "j${type.trim()}"
    "void" -> "void"
    //    ttype.contains("[]") ->
    //        mapTypeToC(ttype.replaceFirst("[]", ""), true) + if (!recursed) "Array" else ""
    "java.lang.Class" -> "jclass"
    "java.lang.String" -> "jstring"
    "java.lang.Throwable" -> "jthrowable"
    else -> "jobject"
  }
}

internal fun mapTypeToD(type: String): String {
  return when (type.trim()) {
    "boolean" -> "Z"
    "byte" -> "B"
    "char" -> "C"
    "short" -> "S"
    "int" -> "I"
    "long" -> "J"
    "float" -> "F"
    "double" -> "D"
    "void" -> "V"
    else ->
        //        if (type.contains("[]")) {
        //          "[" + mapTypeToD(type.replaceFirst("[]", ""))
        //        } else {
        "L" + type.replace('.', '/').trim()
  //        }
  }
}
