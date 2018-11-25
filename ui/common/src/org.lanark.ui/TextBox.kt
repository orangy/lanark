package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.resources.*

class TextBox(
    val rect: Rect,
    val text: String,
    val color: Color,
    val wrap: WordWrap,
    val font: Font
) : Control() {
    
    private val textOrigin = rect.origin + Vector(0, font.baseLine)
    
    override fun contains(point: Point, area: Rect): Boolean {
        return false
    }

    override fun render(dialog: Dialog, frame: Frame) = with(frame) {
        clip(rect.relativeTo(dialog.area)) {
            color(color) {
                when (wrap) {
                    WordWrap.None -> drawText(text, font, textOrigin.relativeTo(dialog.area))
                    WordWrap.Words -> drawTextBox(text, font, rect.relativeTo(dialog.area))
                }
            }
        }
    }
}

enum class WordWrap {
    None, Words
}

fun DialogBuilder.text(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    text: String = "",
    body: TextBoxBuilder.() -> Unit = {}
) = TextBoxBuilder(text, Color.White, WordWrap.None, Rect(x, y, width, height), this, "font").apply(body).build()

class TextBoxBuilder(
    var text: String,
    var color: Color,
    var wrap: WordWrap,
    val rect: Rect,
    val dialog: DialogBuilder,
    var fontName: String = dialog.fontName
) {
    fun build() = TextBox(rect, text, color, wrap, dialog.resources.font(fontName)).also { dialog.add(it) }
}
