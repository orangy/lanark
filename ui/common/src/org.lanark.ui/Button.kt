package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.resources.*

class Button(val position: Point, val text: String, private val resources: ResourceContext) : Control() {
    val area get() = Rect(position, button.size)

    override fun contains(point: Point, area: Rect): Boolean {
        return point in area
    }

    private val font = resources.font("font")
    private val tiles = resources.tiles("elements")
    private val button = tiles["button"]
    private val buttonHover = tiles["button-hover"]
    private val buttonPressed = tiles["button-pressed"]
    private val buttonDisabled = tiles["button-disabled"]

    override fun render(frame: Frame, area: Rect) {
        frame.draw(button, position.relativeTo(area.origin))
        val textSize = font.measureText(text)
        val buttonSize = button.size // { 212 , 39 }
        val textPosition = ((buttonSize - textSize) / 2.0).toVector()
        frame.drawText(text, font, (position + textPosition).relativeTo(area.origin))
    }
}

fun DialogBuilder.button(x: Int, y: Int, text: String = "", body: ButtonBuilder.() -> Unit = {}) =
    ButtonBuilder(Point(x, y), text, this).apply(body).build()

class ButtonBuilder(val position: Point, var text: String = "", val dialog: DialogBuilder) {
    fun build() = Button(position, text, dialog.resources).also { dialog.add(it) }
}
