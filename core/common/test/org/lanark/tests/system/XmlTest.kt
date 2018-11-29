package org.lanark.tests.system

import org.lanark.io.*
import org.lanark.system.*
import org.lanark.xml.*
import kotlin.test.*

//@Ignore
class XmlTest {
    val fs = FileSystems.Default
    // workaround for different test directories for JVM & Native
    private val root = fs.currentDirectory()
        .removeSuffix("build/test-results")
        .removeSuffix(".").removeSuffix("/") + "/common/testResources/xml"
    
    @Test
    fun valid() {
        val folder = fs.combine(root, "valid")
        val files = ((1..119).map { it.toZeroPadding(3) + ".xml" } + "017a.xml").map {
            fs.combine(folder, it)
        }
        
        val failed = mutableListOf<String>()
        var firstException = null as Throwable?
        files.forEach { name ->
            try {
                validate(name)
            } catch (e: Throwable) {
                if (firstException == null)
                    firstException = e
                failed.add(name)
            }
        }
        
        println(failed.joinToString("\n"))
        firstException?.let { throw it }
    }
    
    @Test
    fun invalid() {
        val folder = fs.combine(root, "invalid")
        val files = (1..186).map { it.toZeroPadding(3) + ".xml" }.map {
            fs.combine(folder, it)
        }
        
        val passed = mutableListOf<String>()
        files.forEach { name ->
            try {
                validate(name)
                passed.add(name)
            } catch (e: Throwable) {
            }
        }
        
        if (passed.isNotEmpty()) {
            println(passed.joinToString("\n"))
            fail("These files shouldn't parse")
        }
    }
    
    @Test
    fun debug() {
        val file = fs.combine(root, "valid/007.xml")
        validate(file)
    }

    private fun validate(name: String) {
        fs.open(name, FileOpenMode.Read).use { file ->
            XmlReader(file.input()).scanXml()
        }
    }
}

class TestFailedException(fileName: String, exception: Throwable) :
    Throwable("Failed to process $fileName", exception)

private fun XmlReader.scanXml() {
    loop@ while (true) {
        val token = peek()
        val value : Any = when (token) {
            XmlReader.XmlToken.ELEMENT_BEGIN -> beginElement()
            XmlReader.XmlToken.ELEMENT_NAME -> nextElementName()
            XmlReader.XmlToken.ELEMENT_END -> endElement()
            XmlReader.XmlToken.ATTRIBUTE_NAME -> nextAttributeName()
            XmlReader.XmlToken.ATTRIBUTE_VALUE -> nextAttributeValue()
            XmlReader.XmlToken.ELEMENT_TEXT_CONTENT -> nextTextContent()
            XmlReader.XmlToken.END_OF_DOCUMENT -> break@loop
        }
    }
}
