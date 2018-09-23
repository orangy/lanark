package org.lanark.tests.system

import org.lanark.io.*
import org.lanark.system.*
import kotlin.test.*

class FileTest {
    @Test
    fun readFile() {
        FileSystems.Default.open("testFile.json", FileOpenMode.Read).use { file ->
            val array = file.read(10)
            assertEquals(listOf<Byte>(123, 10, 32, 32, 34, 118, 97, 108, 117, 101), array.toList())
        }
    }

    @Test
    fun writeFile() {
        FileSystems.Default.open("testFile-temp.txt", FileOpenMode.Truncate).use { file ->
            file.write(byteArrayOf(1,2,3))
        }

        FileSystems.Default.open("testFile-temp.txt", FileOpenMode.Read).use { file ->
            val array = file.read(3)
            assertEquals(listOf<Byte>(1, 2, 3), array.toList())
        }
    }
}