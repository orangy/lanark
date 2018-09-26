package org.lanark.tests.lwjgl

import org.junit.Test
import kotlin.test.*

class ResourcesTest {
    @Test
    fun loadResources() {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("testFile.json")
        assertTrue(stream.bufferedReader().readText().length > 10)
    }
}