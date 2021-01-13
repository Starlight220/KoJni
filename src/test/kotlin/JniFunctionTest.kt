package io.github.starlight.kojni

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class JniFunctionTest {
    @Test
    fun testConvertD() =
        assertAll(
            { assertEquals("I", mapTypeToD("int")) },
            { assertEquals("[I", mapTypeToD("int[]")) },
        )
}