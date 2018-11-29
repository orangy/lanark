package org.lanark.xml

import kotlinx.io.*
import kotlinx.io.core.*

/**
 * Original code from https://github.com/Tickaroo/tikxml/blob/master/core/src/main/java/com/tickaroo/tikxml/
 */

/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 * Copyright (C) 2015 Square, Inc.
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * A class to read and parse an xml stream.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
class XmlReader(private val source: Input) : Closeable {

    /** The input XML.  */
    private var peeked = PEEKED_NONE

    private var pathNames = arrayOfNulls<String>(32)
    private var pathIndices = IntArray(32)

    /*
   * The nesting stack. Using a manual array rather than an ArrayList saves 20%.
   */
    private var stack = IntArray(32)
    private var stackSize = 0

    /**
     * Returns a XPath to the current location in the XML value.
     */
    val path: String
        get() = XmlScope.getPath(stackSize, stack, pathNames, pathIndices)

    init {
        stack[stackSize++] = XmlScope.EMPTY_DOCUMENT
    }

    /**
     * Get the next token without consuming it.
     *
     * @return [XmlToken]
     */
    fun peek(): XmlToken {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        return when (p) {
            PEEKED_ELEMENT_BEGIN -> XmlToken.ELEMENT_BEGIN
            PEEKED_ELEMENT_NAME -> XmlToken.ELEMENT_NAME
            PEEKED_ELEMENT_END -> XmlToken.ELEMENT_END
            PEEKED_ATTRIBUTE_NAME -> XmlToken.ATTRIBUTE_NAME
            PEEKED_DOUBLE_QUOTED, PEEKED_SINGLE_QUOTED -> XmlToken.ATTRIBUTE_VALUE
            PEEKED_TEXT_CONTENT, PEEKED_CDATA, PEEKED_ENTITY -> XmlToken.ELEMENT_TEXT_CONTENT
            PEEKED_EOF -> XmlToken.END_OF_DOCUMENT
            else -> throw AssertionError("Unknown XmlToken: Peeked = $p")
        }
    }

    /**
     * Actually do a peek. This method will return the peeked token and updates the internal variable
     * [.peeked]
     *
     * @return The peeked token
     * @throws IOException
     */
    private fun doPeek(): Int {
        val peekStack = stack[stackSize - 1]// consume '
        when (peekStack) {
            XmlScope.ELEMENT_OPENING -> {
                val c = nextNonWhitespace()
                if (!isLiteral(c))
                    throw syntaxError("Expected xml element name (literal expected)")

                peeked = PEEKED_ELEMENT_NAME
                return peeked
            }

            XmlScope.ELEMENT_ATTRIBUTE -> {
                var c = nextNonWhitespace()

                if (isLiteral(c)) {
                    peeked = PEEKED_ATTRIBUTE_NAME
                    return peeked
                }

                when (c) {
                    '>' -> {
                        // remove XmlScope.ELEMENT_ATTRIBUTE from top of the stack
                        popStack()

                        // set previous stack from XmlScope.ELEMENT_OPENING to XmlScope.ELEMENT_CONTENT
                        stack[stackSize - 1] = XmlScope.ELEMENT_CONTENT
                        source.readByte() // consume '>'

                        val nextChar = nextNonWhitespace()

                        if (nextChar == '&') {
                            peeked = PEEKED_ENTITY
                            return peeked
                        }

                        if (nextChar != '<') {
                            peeked = PEEKED_TEXT_CONTENT
                            return peeked
                        }

                        val isCDATA = source.peekEquals(CDATA_OPEN)
                        if (isCDATA) {
                            source.discard(9) // skip opening cdata tag
                            peeked = PEEKED_CDATA
                            return peeked
                        }
                    }

                    '/' -> {
                        // Self closing />
                        source.readByte() // consume '/'

                        if (source.readByte() == '>'.toByte()) {
                            // remove XmlScope.ELEMENT_ATTRIBUTE from top of the stack
                            popStack()

                            peeked = PEEKED_ELEMENT_END
                            return peeked
                        } else {
                            throw syntaxError("Expected closing />")
                        }
                    }
                    '=' -> {
                        source.readByte() // consume '='

                        // Read next char which should be a quote
                        c = nextNonWhitespace()

                        when (c) {
                            '"' -> {
                                source.readByte() // consume "
                                peeked = PEEKED_DOUBLE_QUOTED
                                return peeked
                            }
                            '\'' -> {
                                source.readByte() // consume '
                                peeked = PEEKED_SINGLE_QUOTED
                                return peeked
                            }

                            else -> throw syntaxError("Expected double quote (\") or single quote (') while reading xml elements attribute")
                        }
                    }

                    else -> throw syntaxError("Unexpected character '$c' while trying to read xml elements attribute")
                }
            }
            XmlScope.ELEMENT_CONTENT -> {
                val c = nextNonWhitespace()

                if (c == '&') {
                    peeked = PEEKED_ENTITY
                    return peeked
                }

                if (c != '<') {
                    peeked = PEEKED_TEXT_CONTENT
                    return peeked
                }

                val isCDATA = source.peekEquals(CDATA_OPEN)
                if (isCDATA) {
                    source.discard(9) // skip opening cdata tag
                    peeked = PEEKED_CDATA
                    return peeked
                }
            }
            XmlScope.EMPTY_DOCUMENT -> stack[stackSize - 1] = XmlScope.NONEMPTY_DOCUMENT
            XmlScope.NONEMPTY_DOCUMENT -> {
                try {
                    val c = nextNonWhitespace()
                } catch (e: EOFException) {
                    peeked = PEEKED_EOF
                    return peeked
                }
            }
            XmlScope.CLOSED -> throw IllegalStateException("XmlReader is closed")
        }

        val c = nextNonWhitespace(peekStack == XmlScope.EMPTY_DOCUMENT)
        when (c) {

            // Handling open < and closing </
            '<' -> {
                source.discard(1L)
                val next = source.peekChar()
                // Check if </ which means end of element
                if (next == '/') {
                    source.readByte() // consume /

                    // Check if it is the corresponding xml element name
                    val closingElementName = nextName()
                    if (closingElementName == pathNames[stackSize - 1]) {
                        if (nextNonWhitespace() == '>') {
                            source.readByte() // consume >
                            peeked = PEEKED_ELEMENT_END
                            return peeked
                        } else {
                            syntaxError("Missing closing '>' character in </${pathNames[stackSize - 1]}")
                        }
                    } else {
                        syntaxError("Expected a closing element tag </${pathNames[stackSize - 1]}> but found </$closingElementName>")
                    }
                }
                // its just a < which means begin of the element
                peeked = PEEKED_ELEMENT_BEGIN
                return peeked
            }

            '"' -> {
                source.discard(1L)
                peeked = PEEKED_DOUBLE_QUOTED
                return peeked
            }

            '\'' -> {
                source.discard(1L)
                peeked = PEEKED_SINGLE_QUOTED
                return peeked
            }
        }

        return PEEKED_NONE
    }

    /**
     * Consumes the next token from the Xml stream and asserts that it is the beginning of a new
     * object.
     */

    fun beginElement() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_ELEMENT_BEGIN) {
            pushStack(XmlScope.ELEMENT_OPENING)
            peeked = PEEKED_NONE
        } else {
            throw XmlDataException("Expected ${XmlToken.ELEMENT_BEGIN} but was ${peek()} at path $path")
        }
    }

    /**
     * Consumes the next token from the Xml stream and asserts that it is the end of the current
     * object.
     */

    fun endElement() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p == PEEKED_ELEMENT_END) {
            popStack()
            peeked = PEEKED_NONE
        } else {
            throw syntaxError("Expected end of element but was " + peek())
        }
    }

    /**
     * Checks if there is one more unconsumed xml element that can be consumed afterwards with [ ][.beginElement]
     *
     * @return true if there is at least one more unconsumed xml element, otherwise false
     */

    fun hasElement(): Boolean {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        return p == PEEKED_ELEMENT_BEGIN
    }

    /**
     * Returns true if the current xml element has an unparsed attribute. [.beginElement] must
     * be called before invoking this method
     *
     * @return true if at least one more attribute available, otherwise false
     */

    fun hasAttribute(): Boolean {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        return p == PEEKED_ATTRIBUTE_NAME
    }

    /**
     * Consumes the next token attribute of a xml element. Assumes that [.beginElement] has
     * been called before
     *
     * @return The name of the attribute
     */

    fun nextAttributeName(): String {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p != PEEKED_ATTRIBUTE_NAME) {
            throw syntaxError("Expected xml element attribute name but was " + peek())
        }

        val result = nextName()
        peeked = PEEKED_NONE
        pathNames[stackSize - 1] = result
        return result
    }

    /**
     * Consumes the next attribute's value. Assumes that [.nextAttributeName] has been called
     * before invoking this method
     *
     * @return The value of the attribute as string
     */

    fun nextAttributeValue(): String {

        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_SINGLE_QUOTED) {
            val attributeValue = nextQuotedValue(if (p == PEEKED_DOUBLE_QUOTED) DOUBLE_QUOTE else SINGLE_QUOTE)

            peeked = PEEKED_NONE
            pathNames[stackSize - 1] = null
            // Remove attribute name from stack, do that after nextQuotedValue() to ensure that xpath is correctly in case that nextQuotedValue() fails
            return attributeValue
        } else {
            throw XmlDataException("Expected xml element attribute value (in double quotes or single quotes) but was ${peek()} at path $path")
        }
    }

    /**
     * Consumes the next attribute's value and returns it as an integer. Assumes that [ ][.nextAttributeName] has been called before invoking this method
     *
     * @return the attributes value as an integer
     * @throws IOException
     */

    fun nextAttributeValueAsInt(): Int {
        // TODO natively support integer
        return nextAttributeValue().toInt()
    }

    /**
     * Consumes the next attribute's value and returns it as long. Assumes that [ ][.nextAttributeName] has been called before invoking this method
     *
     * @return the attributes value as an long
     * @throws IOException
     */

    fun nextAttributeValueAsLong(): Long {
        // TODO natively support long
        return nextAttributeValue().toLong()
    }

    /**
     * Consumes the next attribute's value and returns it as boolean. Assumes that [ ][.nextAttributeName] has been called before invoking this method
     *
     * @return the attributes value as an boolean
     * @throws IOException
     */

    fun nextAttributeValueAsBoolean(): Boolean {
        // TODO natively support
        return nextAttributeValue().toBoolean()
    }


    fun nextAttributeValueAsDouble(): Double {
        // TODO natively support
        return nextAttributeValue().toDouble()
    }

    /**
     * Skip the value of an attribute if you don't want to read the value. [ ][.nextAttributeName] must be called before invoking this method
     *
     * @throws IOException
     */

    fun skipAttributeValue() {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_SINGLE_QUOTED) {
            peeked = PEEKED_NONE
            pathNames[stackSize - 1] = null // Remove attribute name from stack
            skipQuotedValue(if (p == PEEKED_DOUBLE_QUOTED) DOUBLE_QUOTE else SINGLE_QUOTE)
        } else {
            throw XmlDataException("Expected xml element attribute value (in double quotes or single quotes) but was ${peek()} at path $path")
        }
    }

    /**
     * Skip the entire attribute (attribute name and attribute value)
     *
     * @throws IOException
     */
    fun skipAttribute() {
        nextAttributeName()
        skipAttributeValue()
    }

    /**
     * Returns true if the current xml element  has another a body which contains either a value or
     * other child xml elements ( objects )
     */
    fun hasTextContent(): Boolean {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        return p == PEEKED_TEXT_CONTENT || p == PEEKED_CDATA || p == PEEKED_ENTITY
    }

    /**
     * Get the next text content of an xml element. Text content is `<element>text
     * content</element>`
     *
     * If the element is empty (no content) like `<element></element>` this method will return
     * the empty string "".
     *
     * `null` as return type is not supported yet, because there is no way in xml to distinguish
     * between empty string "" or null since both might be represented with `<element></element>`. So if you want to represent a null element, simply don't write the
     * corresponding xml tag. Then the parser will not try set the mapped field and it will remain the
     * default value (which is null).
     *
     * @return The xml element's text content
     * @throws IOException
     */
    fun nextTextContent(): String {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        when (p) {
            PEEKED_ENTITY -> {
                peeked = PEEKED_NONE
                source.discard(1) // discard '&'
                val first = source.peekChar()
                if (first == '#') {
                    source.discard(1) // discard '#'
                    val hex = source.peekChar()
                    if (hex == 'x')
                        source.discard(1)
                    val value = source.readUTF8UntilDelimiter(NAME_TERMINALS)
                    val unicode = value.toIntOrNull(if (hex=='x') 16 else 10)
                    if (unicode == null)
                        throw syntaxError("Invalid entity unicode value '$value'.")
                    val c = source.readByte().toChar()
                    if (c != ';')
                        throw syntaxError("Unterminated entity '&#$value'. Expected ';' but got '$c'")
                    return unicode.toChar().toString()

                } else {
                    val name = nextName()
                    if (source.endOfInput) {
                        throw syntaxError("Unterminated entity '&$name'. Expected ';' but arrived at end of input")
                    }
                    val c = source.readByte().toChar()
                    if (c != ';')
                        throw syntaxError("Unterminated entity '&$name'. Expected ';' but got '$c'")
                    return translateEntity(name)
                }
            }
            PEEKED_TEXT_CONTENT -> {
                peeked = PEEKED_NONE
    
                // Read text until '<' or '&' found
                val text = source.readUTF8UntilDelimiter("<&")
                if (source.endOfInput) {
                    throw syntaxError("Unterminated element text content. Expected </${pathNames[stackSize - 1]}> but haven't found")
                }
    
                return text
            }
            PEEKED_CDATA -> {
                peeked = PEEKED_NONE
    
                return source.readToText(CDATA_CLOSE)
            }
            else -> return if (p == PEEKED_ELEMENT_END) {
                // this is an element without any text content. i.e. <foo></foo>.
                // In that case we return the default value of a string which is the empty string
    
                // Don't do peeked = PEEKED_NONE; because that would consume the end tag, which we haven't done yet.
                ""
            } else {
                throw XmlDataException("Expected xml element text content but was ${peek()} at path $path")
            }
        }
    }

    private fun translateEntity(name: String): String = when(name) {
        "amp" -> "&"
        "quot" -> "\""
        "apos" -> "'"
        "lt" -> "<"
        "gt" -> ">"
        // TODO: support doctype
        else -> name // throw syntaxError("Unknown entity '$name'")
    }

    /**
     * Get the next text content of an xml element as integer. Text content is `<element>123</element>`
     *
     * @return The xml element's text content as integer or 0 if empty tag like `<element></element>`
     * @throws IOException
     */
    fun nextTextContentAsInt(): Int {
        // TODO natively support

        // case when <element></element>  is empty, then return default value which is "0" for long
        val content = nextTextContent()
        return if (content == "") {
            0
        } else content.toInt()

    }

    /**
     * Get the next text content of an xml element as long. Text content is `<element>123</element>`
     *
     * @return The xml element's text content as long or 0 if empty tag like `<element></element>`
     * @throws IOException
     */
    fun nextTextContentAsLong(): Long {
        // TODO natively support

        // case when <element></element>  is empty, then return default value which is "0" for long
        val content = nextTextContent()
        return if (content == "") {
            0
        } else content.toLong()

    }

    /**
     * Get the next text content of an xml element as double. Text content is `<element>123</element>`
     *
     * @return The xml element's text content as double or 0.0 if empty tag like `<element></element>`
     * @throws IOException
     */
    fun nextTextContentAsDouble(): Double {
        // TODO natively support

        // case when <element></element>  is empty, then return default value which is "0.0" for double
        val content = nextTextContent()
        return if (content == "") {
            0.0
        } else content.toDouble()

    }

    /**
     * Get the next text content of an xml element as boolean. Text content is `<element>123</element>`
     *
     * @return The xml element's text content as boolean or false if empty tag like `<element></element>`
     * @throws IOException
     */
    fun nextTextContentAsBoolean(): Boolean {
        // TODO natively support

        // case when <element></element>  is empty, then return default value which is "false" for boolean
        val content = nextTextContent()
        return if (content == "") {
            false
        } else content.toBoolean()
    }

    /**
     * Skip the text content. Text content is `<element>text content</element>`
     *
     * @throws IOException
     */

    fun skipTextContent() {

        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }

        when (p) {
            PEEKED_ENTITY -> {
                peeked = PEEKED_NONE
                val name = nextName()
                if (source.endOfInput) {
                    throw syntaxError("Unterminated entity '$name'. Expected ';' but arrived at end of input")
                }
                val c = source.readByte().toChar()
                if (c != ';')
                    throw syntaxError("Unterminated entity '$name'. Expected ';' but got '$c'")
            }
            PEEKED_TEXT_CONTENT -> {
                peeked = PEEKED_NONE

                // Read text until '<' found
                source.discardUntilDelimiter(OPENING_XML_ELEMENT.toByte())
                if (source.endOfInput) {
                    throw syntaxError("Unterminated element text content. Expected </${pathNames[stackSize - 1]}> but haven't found")
                }
            }
            PEEKED_CDATA -> peeked = PEEKED_NONE
            // Search index of closing CDATA tag ]]>
            /*
                        val index = indexOfClosingCDATA()
                        source.discard(index + 3) // +3 because of consuming closing tag
            */
            else -> throw XmlDataException(
                "Expected xml element text content but was " + peek()
                        + " at path " + path
            )
        }
    }

    /**
     * Push a new scope on top of the scope stack
     *
     * @param newTop The scope that should be pushed on top of the stack
     */
    private fun pushStack(newTop: Int) {
        if (stackSize == stack.size) {
            val newStack = IntArray(stackSize * 2)
            val newPathIndices = IntArray(stackSize * 2)
            val newPathNames = arrayOfNulls<String>(stackSize * 2)
            stack.copyInto(newStack, 0, stackSize)
            pathIndices.copyInto(newPathIndices, 0, stackSize)
            pathNames.copyInto(newPathNames, 0, stackSize)
            stack = newStack
            pathIndices = newPathIndices
            pathNames = newPathNames
        }
        stack[stackSize++] = newTop
    }

    /**
     * Removes the top element of the stack
     */
    private fun popStack() {
        stack[stackSize - 1] = 0
        stackSize--
        pathNames[stackSize] = null // Free the last path name so that it can be garbage collected!
        pathIndices[stackSize - 1]++
    }

    override fun close() {
        peeked = PEEKED_NONE
        source.close()
    }

    /**
     * Returns the next character in the stream that is neither whitespace nor a part of a comment.
     * When this returns, the returned character is always at `buffer[pos-1]`; this means the
     * caller can always pushStack back the returned character by decrementing `pos`.
     */
    private fun nextNonWhitespace(isDocumentBeginning: Boolean = false): Char {
        /*
     * This code uses ugly local variables 'p' and 'l' representing the 'pos'
     * and 'limit' fields respectively. Using locals rather than fields saves
     * a few field reads for each whitespace character in a pretty-printed
     * document, resulting in a 5% speedup. We need to flush 'p' to its field
     * before any (potentially indirect) call to fillBuffer() and reread both
     * 'p' and 'l' after any (potentially indirect) call to the same method.
     */

        // Look for UTF-8 BOM sequence 0xEFBBBF and skip it
        if (isDocumentBeginning && source.peekEquals(UTF8_BOM)) {
            source.discard(3)
        }

        loop@ while (!source.endOfInput) {
            val c = source.peekChar()
            if (c == '\n' || c == ' ' || c == '\r' || c == '\t') {
                source.discard(1L)
                continue
            }

            val isCDATA = source.peekEquals(CDATA_OPEN)
            if (c == '<' && !isCDATA) {
                val peekStack = stack[stackSize - 1]

                when {
                    peekStack == XmlScope.NONEMPTY_DOCUMENT && source.peekEquals(DOCTYPE_OPEN) -> {
                        source.discard(DOCTYPE_OPEN.length.toLong())
                        if (!source.discardToText(DOCTYPE_INLINE_CLOSE))
                            throw syntaxError("Unterminated <!DOCTYPE>. Inline DOCTYPE is not supported at the moment.")

                        // TODO inline DOCTYPE.
                        continue@loop
                    }
                    source.peekEquals("<!--") -> {
                        source.discard(4L)
                        if (!source.discardToText(COMMENT_CLOSE))
                            throw syntaxError("Unterminated comment")
                        if (source.readByte().toChar() != '>')
                            throw syntaxError("Comment cannot contain text --")
                        continue@loop
                    }
                    source.peekEquals("<?") -> {
                        source.discard(2L)
                        nextName() // validate PI has name
                        if (!source.discardToText(PROCESSING_INSTRUCTION_CLOSE))
                            throw syntaxError("Unterminated xml declaration or processing instruction \"<?\"")
                        continue@loop
                    }
                }
            }

            return c
        }

        throw EOFException("Unexpected end of input at path $path")
    }

    /**
     * Throws a new IO exception with the given message and a context snippet with this reader's
     * content.
     */
    private fun syntaxError(message: String): IOException {
        throw IOException("$message at path $path")
    }

    /**
     * Get the name of the opening xml name
     *
     * @return The name
     * @throws IOException
     */
    fun nextElementName(): String {
        var p = peeked
        if (p == PEEKED_NONE) {
            p = doPeek()
        }
        if (p != PEEKED_ELEMENT_NAME) {
            throw syntaxError("Expected XML Tag Element name, but have " + peek())
        }

        val result = nextName()

        peeked = PEEKED_NONE
        pathNames[stackSize - 1] = result

        // Next we expect element attributes block
        pushStack(XmlScope.ELEMENT_ATTRIBUTE)
        return result
    }

    /** Returns an unquoted value as a string.  */
    private fun nextName(): String {
        val value = source.readUTF8UntilDelimiter(NAME_TERMINALS)
        if (value.isEmpty())
            throw syntaxError("XML Name cannot be empty")
        if (value[0] in NAME_NOT_FIRST)
            throw syntaxError("XML Name cannot start with ${value[0]}")
        return value
    }

    /**
     * Returns the string up to but not including `quote`, unescaping any character escape
     * sequences encountered along the way. The opening quote should have already been read. This
     * consumes the closing quote, but does not include it in the returned string.
     *
     * @throws IOException if any unicode escape sequences are malformed.
     */
    private fun nextQuotedValue(delimiter: String): String {
        val value = source.readUTF8UntilDelimiter(delimiter)
        source.discard(1L)
        return value
    }

    /**
     * Checks whether the passed character is a literal or not
     *
     * @param c the character to check
     * @return true if literal, otherwise false
     */
    private fun isLiteral(c: Char): Boolean {
        when (c) {
            '=', '<', '>', '/', ' ' -> return false
            else -> return true
        }
    }

    /**
     * Unescapes the character identified by the character or characters that immediately follow a
     * backslash. The backslash '\' should have already been read. This supports both unicode escapes
     * "u000A" and two-character escapes "\n".
     *
     * @throws IOException if any unicode escape sequences are malformed.
     */
    private fun readEscapeCharacter(): Char {
        if (source.endOfInput) {
            throw syntaxError("Unterminated escape sequence")
        }

        val escaped = source.readByte().toChar()
        when (escaped) {
            'u' -> {
                val chars = source.readTextExact(n = 4)
/*
                if (!fillBuffer(4)) {
                    throw EOFException("Unterminated escape sequence at path $path")
                }
*/
                // Equivalent to Integer.parseInt(stringPool.get(buffer, pos, 4), 16);
                var result: Int = 0
                var i = 0
                val end = i + 4
                while (i < end) {
                    val c = chars[i]
                    result = result shl 4 + when (c) {
                        in '0'..'9' -> (c - '0')
                        in 'a'..'f' -> (c - 'a' + 10)
                        in 'A'..'F' -> (c - 'A' + 10)
                        else -> throw syntaxError("\\u$chars")
                    }
                    i++
                }
                return result.toChar()
            }

            't' -> return '\t'
            'b' -> return '\b'
            'n' -> return '\n'
            'r' -> return '\r'
            '\n', '\'', '"', '\\' -> return escaped
            else -> return escaped
        }
    }

    /**
     * Skip a quoted value
     *
     * @param runTerminator The terminator to skip
     * @throws IOException
     */
    private fun skipQuotedValue(delimiter: String) {
        source.discardUntilDelimiter(delimiter[0].toByte())
    }

    /**
     * This method skips the rest of an xml Element. This method is typically invoked once [ ][.beginElement] ang [.nextElementName] has been consumed, but we don't want to consume
     * the xml element with the given name. So with this method we can  skip the whole remaining xml
     * element (attribute, text content and child elements) by using this method.
     *
     * @throws IOException
     */
    fun skipRemainingElement() {

        val stackPeek = stack[stackSize - 1]
        if (stackPeek != XmlScope.ELEMENT_OPENING && stackPeek != XmlScope.ELEMENT_ATTRIBUTE) {
            throw AssertionError(
                "This method can only be invoked after having consumed the opening element via beginElement()"
            )
        }

        var count = 1
        do {
            when (peek()) {
                XmlReader.XmlToken.ELEMENT_BEGIN -> {
                    beginElement()
                    count++
                }

                XmlReader.XmlToken.ELEMENT_END -> {
                    endElement()
                    count--
                }

                XmlReader.XmlToken.ELEMENT_NAME -> nextElementName() // TODO add a skip element name method

                XmlReader.XmlToken.ATTRIBUTE_NAME -> nextAttributeName() // TODO add a skip attribute name method

                XmlReader.XmlToken.ATTRIBUTE_VALUE -> skipAttributeValue()

                XmlReader.XmlToken.ELEMENT_TEXT_CONTENT -> skipTextContent()

                XmlReader.XmlToken.END_OF_DOCUMENT -> if (count != 0) {
                    throw syntaxError("Unexpected end of file! At least one xml element is not closed!")
                }

                else -> throw AssertionError(
                    "Oops, there is something not implemented correctly internally. Please fill an issue on https://github.com/Tickaroo/tikxml/issues . Please include stacktrace and the model class you try to parse"
                )
            }
            peeked = PEEKED_NONE
        } while (count != 0)
    }

    /**
     * Skip an unquoted value
     *
     * @throws IOException
     *
     * private void skipUnquotedValue() throws IOException { long i = source.indexOfElement(UNQUOTED_STRING_TERMINALS);
     * buffer.skip(i != -1L ? i : buffer.size()); }
     */

    enum class XmlToken {
        /**
         * Indicates that an xml element begins.
         */
        ELEMENT_BEGIN,

        /**
         * xml element name
         */
        ELEMENT_NAME,

        /**
         * Indicates that an xml element ends
         */
        ELEMENT_END,

        /**
         * Indicates that we are reading an attribute name (of an xml element)
         */
        ATTRIBUTE_NAME,

        /**
         * Indicates that we are reading a xml elements attribute value
         */
        ATTRIBUTE_VALUE,

        /**
         * Indicates that we are reading the text content of an xml element like this `<element>
         * This is the text content </element>`
         */
        ELEMENT_TEXT_CONTENT,

        /**
         * Indicates that we have reached the end of the document
         */
        END_OF_DOCUMENT
    }

    companion object {
        private val NAME_TERMINALS = " !\"#\$%&'()*+,/;<=>?@[\\]^`{|}~\n"
        private val NAME_NOT_FIRST = "-.0123456789"

        private val CDATA_CLOSE = "]]>"
        private val CDATA_OPEN = "<![CDATA["
        private val DOCTYPE_OPEN = "<!DOCTYPE"
        private val DOCTYPE_INLINE_CLOSE = "]>"
        private val COMMENT_CLOSE = "--"
        private val PROCESSING_INSTRUCTION_CLOSE = "?>"
        private val UTF8_BOM = String(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

        private val DOUBLE_QUOTE = "\""
        private val SINGLE_QUOTE = "'"
        private val OPENING_XML_ELEMENT = "<"
        private val CLOSING_XML_ELEMENT = ">"

        //
        // Peek states
        //
        /** Nothing peeked  */
        private val PEEKED_NONE = 0
        /** Peeked an xml element / object  */
        private val PEEKED_ELEMENT_BEGIN = 1
        /** Peeked the closing xml tag which indicates the end of an object  */
        private val PEEKED_ELEMENT_END = 2
        /** Peeked the closing xml header tag, hence we are inner xml tag object body  */
        private val PEEKED_TEXT_CONTENT = 3
        /** Peeked the end of the stream  */
        private val PEEKED_EOF = 4
        /** Peeked an unquoted value which can be either xml element name or element attribute name  */
        private val PEEKED_ELEMENT_NAME = 5
        /** Peeked a quoted value which is the value of an xml attribute  */
        private val PEEKED_DOUBLE_QUOTED = 6
        /** Peeked a single quote which is the value of an xml attribute  */
        private val PEEKED_SINGLE_QUOTED = 7
        /** Peeked an attribute name (of a xml element)  */
        private val PEEKED_ATTRIBUTE_NAME = 8
        /** Peeked a CDATA  */
        private val PEEKED_CDATA = 9
        /** Peeked a CDATA  */
        private val PEEKED_ENTITY = 10
    }
}

private fun Input.discardToText(text: String): Boolean {
    while (!endOfInput) {
        if (peekEquals(text)) {
            discard(text.length.toLong())
            return true
        }
        discard(1)
    }
    return false
}

private fun Input.readToText(text: String) = buildString {
    while (!endOfInput) {
        if (peekEquals(text)) {
            discard(text.length.toLong())
            break
        }
        readText(this, max = 1)
    }
}

private fun Input.peekChar(): Char {
    var value = '?'
    takeWhile {
        value = it.readByte().toChar()
        it.pushBack(1)
        false
    }
    return value
}

private fun Input.peekEquals(text: String): Boolean {
    var equals = false
    takeWhileSize(text.length) { buffer ->
        val remaining = buffer.readRemaining
        val sourceText = buffer.readText(max = text.length)
        equals = sourceText == text
        buffer.pushBack(remaining - buffer.readRemaining)
        0
    }
    return equals
}

/**
 * Returns the next character in the stream that is neither whitespace nor a part of a comment.
 * When this returns, the returned character is always at `buffer[pos-1]`; this means the
 * caller can always pushStack back the returned character by decrementing `pos`.
 */


/**
 * Lexical scoping elements within a XML reader or writer.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
internal object XmlScope {

    /** No object or array has been started.  */
    val EMPTY_DOCUMENT = 0

    /** A document at least one object  */
    val NONEMPTY_DOCUMENT = 1

    // /** XML declaration like {@code <?xml version="1.0" encoding="UTF-8"?>} */
    // static final int XML_DECLARATION = 2;

    /** We are in the opening xml tag like `<element>`  */
    val ELEMENT_OPENING = 3

    /** We are in the scope of reading attributes of a given element  */
    val ELEMENT_ATTRIBUTE = 4

    /**
     * We are in an elment's content (between opening and closing xml element tag) like `<element>HERE WE ARE</element>`
     */
    val ELEMENT_CONTENT = 5

    /**
     * A document that's been closed and cannot be accessed.
     */
    val CLOSED = 6

    /**
     * Prints the XmlScope (mainly for debugging) for the element that is on top of the stack
     *
     * @param stackSize The size of the stack
     * @param stack The stack itself
     * @return String representing the XmlScope on top of the stack
     */
    fun getTopStackElementAsToken(stackSize: Int, stack: IntArray): String {
        when (stack[stackSize - 1]) {
            ELEMENT_OPENING -> return "ELEMENT_OPENING"
            EMPTY_DOCUMENT -> return "EMPTY_DOCUMENT"
            NONEMPTY_DOCUMENT -> return "NONEMPTY_DOCUMENT"
            ELEMENT_ATTRIBUTE -> return "ELEMENT_ATTRIBUTE"
            ELEMENT_CONTENT -> return "ELEMENT_CONTENT"
            CLOSED -> return "CLOSED"
            else -> throw IOException("Unexpected token on top of the stack. Was " + stack[stackSize - 1])
        }
    }

    /**
     * Renders the path in a JSON document to a string. The `pathNames` and `pathIndices`
     * parameters corresponds directly to stack: At indices where the stack contains an object
     * (EMPTY_OBJECT, DANGLING_NAME or NONEMPTY_OBJECT), pathNames contains the name at this scope.
     * Where it contains an array (EMPTY_ARRAY, NONEMPTY_ARRAY) pathIndices contains the current index
     * in that array. Otherwise the value is undefined, and we take advantage of that by incrementing
     * pathIndices when doing so isn't useful.
     */
    fun getPath(stackSize: Int, stack: IntArray, pathNames: Array<String?>, pathIndices: IntArray): String {
        val result = StringBuilder()
        var i = 0
        while (i < stackSize) {
            when (stack[i]) {
                ELEMENT_OPENING -> {
                    result.append('/')
                    if (pathNames[i] != null) {
                        result.append(pathNames[i])
                    }
                }

                ELEMENT_CONTENT -> {
                    result.append('/')
                    if (pathNames[i] != null) {
                        result.append(pathNames[i])
                        if (i == stackSize - 1) {
                            result.append("/text()")
                        }
                    }
                }

                ELEMENT_ATTRIBUTE -> if (pathNames[i] != null) {
                    result.append("[@")
                    result.append(pathNames[i])
                    result.append(']')
                }
                NONEMPTY_DOCUMENT, EMPTY_DOCUMENT, CLOSED -> {
                }
            }
            i++
        }
        return if (result.length == 0) "/" else result.toString()
    }
}

class XmlDataException(message: String) : Exception(message)

