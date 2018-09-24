package org.lanark.tests.lwjgl

import org.junit.*

class ResourcesTest {
    @Test
    fun loadResources() {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("testFile.json")
        println(stream.bufferedReader().readText())
    }
}