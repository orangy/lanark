package org.lanark.drawing

import kotlinx.serialization.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.system.*

class Font(val texture: Texture, descriptor: FontDescriptor) : Managed {
    val lineHeight = descriptor.lineHeight
    val encoding = descriptor.chars.associateBy { it.id }

    override fun release() {
        texture.release()
    }

    override fun toString() = "Font $texture"
}

fun Font.measureText(text: String): Int {
    val encoding = encoding
    var x = 0
    text.forEach {
        val char = encoding[it.toInt()] ?: return@forEach // continue
        x += char.xadvance
    }
    return x
}

fun Frame.drawTextBox(text: String, font: Font, rect: Rect) {
    clip(rect) {
        var x = rect.left
        var y = rect.top
        val words = text.split(' ')
        val spaceWidth = font.encoding[32]?.xadvance ?: 16
        words.forEach { word ->
            val width = font.measureText(word)
            if (x + width > rect.right) {
                x = rect.left
                y += font.lineHeight
                if (y > rect.bottom)
                    return
            }
            drawText(word, font, x, y)
            x += width + spaceWidth
        }
    }
}

fun Frame.drawText(text: String, font: Font, point: Point) = drawText(text, font, point.x, point.y)

fun Frame.drawText(text: String, font: Font, x: Int, y: Int) {
    val texture = font.texture
    val encoding = font.encoding
    var x = x
    val y = y
    text.forEach {
        val char = encoding[it.toInt()] ?: run {
            engine.logger.trace("Character '$it' is not present in font")
            return@forEach // continue
        }
        val srcRect = Rect(char.x, char.y, char.width, char.height)
        val dstRect = Rect(x + char.xoffest, y + char.yoffset, char.width, char.height)
        draw(texture, srcRect, dstRect)
        x += char.xadvance
    }
}


@Serializable
data class FontDescriptor(val imagePath: String, val lineHeight: Int, val chars: List<FontChar>)

@Serializable
data class FontChar(
    val id: Int,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val xadvance: Int,
    @Optional val xoffest: Int = 0,
    @Optional val yoffset: Int = 0
)