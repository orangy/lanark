package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.system.*

class Font(
    val texture: Texture,
    val name: String,
    val baseLine: Int,
    val lineHeight: Int,
    val characters: List<FontCharacter>
) : Managed {
    val charMap = characters.associateBy { it.code }

    fun kerning(character: FontCharacter, previous: FontCharacter): Int {
        val pair = character.kerning.firstOrNull { it.previous == previous.code } ?: return 0
        return pair.amount
    }

    fun measureText(text: String): Size {
        val charMap = charMap
        var x = 0
        var h = 0
        var previousChar: FontCharacter? = null
        text.forEach { c ->
            val char = charMap[c.toInt()] ?: return@forEach // continue
            val kerning = previousChar?.let { kerning(char, it) } ?: 0
            x += char.xadvance + kerning
            h = maxOf(h, char.textureRect.height)
            previousChar = char
        }
        return Size(x, h)
    }

    override fun release() {
        texture.release()
    }

    override fun toString() = "Font $texture"
}


fun Frame.drawTextBox(text: String, font: Font, rect: Rect) {
    var x = rect.left
    var y = rect.top + font.baseLine
    val width = rect.width
    val words = text.split(' ')
    val spaceWidth = font.charMap[32]?.xadvance ?: 16
    words.forEach { word ->
        val textSize = font.measureText(word)
        if (textSize.width > width) {
            // word is wider than rectangle width, wrap at characters
            //TODO()
        }

        if (x + textSize.width > rect.right) {
            x = rect.left
            y += font.lineHeight
            if (y >= rect.bottom)
                return
        }
        drawText(word, font, x, y)
        x += textSize.width + spaceWidth
    }
}

fun Frame.drawText(text: String, font: Font, point: Point) = drawText(text, font, point.x, point.y)

fun Frame.drawText(text: String, font: Font, x: Int, baseLine: Int) {
    val texture = font.texture
    val charMap = font.charMap
    val y = baseLine - font.baseLine
    var current = x
    var previousChar: FontCharacter? = null
    text.forEach { c ->
        val char = charMap[c.toInt()] ?: return@forEach // no such char in font, TODO: draw square
        val kerning = previousChar?.let { font.kerning(char, it) } ?: 0
        val srcRect = char.textureRect
        val dstRect = Rect(current + char.xoffest + kerning, y + char.yoffset, srcRect.width, srcRect.height)

        draw(texture, srcRect, dstRect)
        current += char.xadvance + kerning
        previousChar = char
    }
}

data class FontCharacter(
    val code: Int,
    val textureRect: Rect,
    val xoffest: Int,
    val yoffset: Int,
    val xadvance: Int,
    val kerning: List<FontCharacterKerning>
)

data class FontCharacterKerning(val previous: Int, val amount: Int)
