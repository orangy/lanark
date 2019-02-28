package org.lanark.playground

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.math.*

class Bouncer(
    private val tile: Tile,
    initialPosition: Vector2,
    private val minPosition: Float,
    private val maxPosition: Float,
    private val speed: Float,
    private val normalCursor: Cursor,
    private val hotCursor: Cursor
) {
    private val itemPosition = mutableVectorOf(initialPosition.x, initialPosition.y)

    suspend fun run(engine: Engine) {
        while (true) {
            while (itemPosition.x < maxPosition) {
                val dt = engine.nextTick()
                itemPosition += vectorOf(speed * dt, 0f)
            }
            while (itemPosition.x > minPosition) {
                val dt = engine.nextTick()
                itemPosition -= vectorOf(speed * dt, 0f)
            }
        }
    }

    fun render(frame: Frame) {
        frame.draw(tile, itemPosition.toPoint())
    }

    fun handle(frame: Frame, event: Event): Boolean {
        when (event) {
            is EventMouseMotion -> {
                if (event.position in Rect(itemPosition.toPoint(), tile.size)) {
                    frame.cursor = hotCursor
                    return true
                }
            }
        }
        return false
    }
}

private fun Vector2.toPoint() = Point(x.toInt(), y.toInt())
