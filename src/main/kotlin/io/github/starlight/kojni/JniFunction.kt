package io.github.starlight.kojni

class JniFunction(
    private val fqcontainer: String,
    private val isStatic: Boolean,
    private val methodName: String,
    private val retVal: String,
    args: List<String>?
) {
  private val c_args: String by lazy {
    args?.joinToString(separator = ", ", prefix = ", ", transform = ::mapTypeToC) ?: ""
  }
  private val d_args: String by lazy {
    args?.joinToString(separator = "", transform = ::mapTypeToD) ?: ""
  }
  private val c_args_named: String by lazy {
    args
        ?.mapIndexed { index: Int, s: String -> mapTypeToC(s) + " param${'$'}${index}" }
        ?.joinToString(prefix = ", ", separator = ", ")
        ?: ""
  }

  fun buildFunction(): String =
      """
      /*
       * Class:     ${fqcontainer}
       * Method:    ${methodName}
       * Signature: (${d_args})${mapTypeToD(retVal)}
       */
      JNIEXPORT ${mapTypeToC(retVal)} JNICALL Java_${fqcontainer}_${methodName}
        (JNIEnv *, ${if (isStatic) "jclass" else "jobject"}${c_args});
  """

  fun impl(): String =
      """
    JNIEXPORT ${mapTypeToC(retVal)} JNICALL Java_${fqcontainer}_${methodName}
      (JNIEnv *env, ${if (isStatic) "jclass clazz" else "jobject thisObj"}${c_args_named}) {}
  """
}

private val javaPrimitives =
    setOf("boolean", "byte", "char", "short", "int", "long", "float", "double")

internal fun mapTypeToC(type: String): String {
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
