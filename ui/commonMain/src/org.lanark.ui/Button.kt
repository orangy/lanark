package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*

class Button(val position: Point, val text: String, private val resources: ResourceContext) : Control() {
    val area get() = Rect(position, button.size)

    override fun contains(point: Point, area: Rect): Boolean {
        return point in area
    }

    private val font = resources.font("font")
    private val tiles = resources.tiles("elements")
    private val button = tiles["button-hover"]
    private val buttonHover = tiles["button"]
    private val buttonPressed = tiles["button-pressed"]
    private val buttonDisabled = tiles["button-disabled"]
    private val baseLine = 18 + font.baseLine

    var state = State.Normal

    enum class State {
        Normal, Hover, Pressed, Disabled
    }

    override fun render(dialog: Dialog, frame: Frame) {
        val texture = when (state) {

            State.Normal -> button
            State.Hover -> buttonHover
            State.Pressed -> buttonPressed
            State.Disabled -> buttonDisabled
        }

        frame.draw(texture, position.relativeTo(dialog.area.origin))
        val textSize = font.measureText(text)
        val buttonSize = button.size // { 212 , 39 }
        val textPosition = Vector(buttonSize.width / 2 - textSize.width / 2, baseLine)
        frame.drawText(text, font, (position + textPosition).relativeTo(dialog.area.origin))
    }

    override fun event(dialog: Dialog, frame: Frame, event: Event): Boolean {
        when (event) {
            is EventMouseMotion -> {
                val buttonRect = area.relativeTo(dialog.area)
                state = if (event.position in buttonRect) {
                    if (state == State.Normal) State.Hover else state
                } else {
                    State.Normal
                }
                return false
            }
            is EventMouseButton -> {
                val buttonRect = area.relativeTo(dialog.area)
                if (event.position in buttonRect) {
                    if (event is EventMouseButtonDown && event.button == MouseButton.Left) {
                        state = State.Pressed
                    } else {
                        state = State.Hover
                    }

                } else {
                    state = State.Normal
                }
            }
        }
        return super.event(dialog, frame, event)
    }
}

fun DialogBuilder.button(x: Int, y: Int, text: String = "", body: ButtonBuilder.() -> Unit = {}) =
    ButtonBuilder(Point(x, y), text, this).apply(body).build()

class ButtonBuilder(val position: Point, var text: String = "", val dialog: DialogBuilder) {
    fun build() = Button(position, text, dialog.resources).also { dialog.add(it) }
}
