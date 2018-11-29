package org.lanark.xml

import kotlinx.io.core.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*

class XmlDecoder(val reader: XmlReader) : ElementValueDecoder() {

    companion object {
        public fun <T> parse(deserializer: DeserializationStrategy<T>, input: Input): T {
            val reader = XmlReader(input)
            val input = XmlDecoder(reader)
            val result = input.decode(deserializer)
            //if (!reader.isDone) { error("Reader has not consumed the whole input: $reader") }
            return result
        }
    }

    var valueElement = false
    override fun decodeElementIndex(desc: SerialDescriptor): Int {
        when (reader.peek()) {
            XmlReader.XmlToken.ATTRIBUTE_NAME -> {
                val name = reader.nextAttributeName()
                val index = desc.getElementIndex(name)
                if (index != CompositeDecoder.UNKNOWN_NAME) {
                    return index
                }
                reader.skipAttributeValue()
            }
            XmlReader.XmlToken.ELEMENT_END -> {
                if (valueElement) {
                    valueElement = false
                    reader.endElement()
                    return decodeElementIndex(desc)
                }
                return CompositeDecoder.READ_DONE
            }
            XmlReader.XmlToken.ELEMENT_BEGIN -> {
                if (valueElement) TODO("BAD STRUCTURE")
                reader.beginElement()
                if (reader.peek() != XmlReader.XmlToken.ELEMENT_NAME) TODO("BAD XML")
                val name = reader.nextElementName()
                val index = desc.getElementIndex(name)
                if (index != CompositeDecoder.UNKNOWN_NAME) {
                    valueElement = true
                    return index
                }
                reader.skipRemainingElement()
            }
        }
        return super.decodeElementIndex(desc)
    }

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
        when (reader.peek()) {
            XmlReader.XmlToken.ELEMENT_BEGIN -> {
                reader.beginElement()
                if (reader.peek() != XmlReader.XmlToken.ELEMENT_NAME) TODO("BAD XML")
                val name = reader.nextElementName()
                if (desc.name.substringAfterLast('.') != name) TODO("BAD ROOT")
                return this
            }
        }
        TODO("BAD XML")
    }

    override fun endStructure(desc: SerialDescriptor) {
        super.endStructure(desc)
    }

    private fun takeString(): String = when (val token = reader.peek()) {
        XmlReader.XmlToken.ATTRIBUTE_VALUE -> reader.nextAttributeValue()
        XmlReader.XmlToken.ELEMENT_TEXT_CONTENT -> reader.nextTextContent()
        else -> TODO("Getting value from invalid token $token")
    }

    override fun decodeBoolean(): Boolean = takeString().toBoolean()
    override fun decodeByte(): Byte = takeString().toByte()
    override fun decodeShort(): Short = takeString().toShort()
    override fun decodeInt(): Int = takeString().toInt()
    override fun decodeLong(): Long = takeString().toLong()
    override fun decodeFloat(): Float = takeString().toFloat()
    override fun decodeDouble(): Double = takeString().toDouble()
    override fun decodeChar(): Char = takeString().single()
    override fun decodeString(): String = takeString()
    override fun decodeEnum(enumDescription: EnumDescriptor): Int = enumDescription.getElementIndex(takeString())

}