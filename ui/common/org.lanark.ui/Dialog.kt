package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*

class Dialog(val area: Rect, private val resources: ResourceContext, val controls: List<Control>) : UIScene() {
    private val background = resources.loadTexture("background")
    private val tiles = resources.loadTiles("elements")

    private val topLeftTile = tiles["border-top-left"]
    private val topRightTile = tiles["border-top-right"]
    private val bottomLeftTile = tiles["border-bottom-left"]
    private val bottomRightTile = tiles["border-bottom-right"]
    private val topTile = tiles["border-top"]
    private val rightTile = tiles["border-right"]
    private val leftTile = tiles["border-left"]
    private val bottomTile = tiles["border-bottom"]

    override fun render(renderer: Renderer) {
        renderer.renderFrame()
        renderer.renderControls()
    }

    private fun Renderer.renderControls() {
        clip(area) {
            controls.forEach {
                it.render(area, this)
            }
        }
    }

    private fun Renderer.renderFrame() {
        fill(background, area)
        draw(topLeftTile, Point(area.left, area.top))
        draw(topRightTile, Point(area.right, area.top))
        draw(bottomLeftTile, Point(area.left, area.bottom))
        draw(bottomRightTile, Point(area.right, area.bottom))

        val topLeftX = topLeftTile.width - topLeftTile.origin.x
        fill(topTile, Rect(
                area.left + topLeftX,
                area.top - topTile.origin.y,
                area.width - topLeftX - topRightTile.origin.x,
                topTile.height))

        val bottomLeftX = bottomLeftTile.width - bottomLeftTile.origin.x
        fill(bottomTile, Rect(
                area.left + bottomLeftX,
                area.bottom - bottomTile.origin.y,
                area.width - bottomLeftX - bottomRightTile.origin.x,
                bottomTile.height))

        val topLeftY = topLeftTile.height - topLeftTile.origin.y
        fill(leftTile, Rect(
                area.left - leftTile.origin.x,
                area.top + topLeftY,
                leftTile.width,
                area.height - topLeftY - bottomLeftTile.origin.y))

        val topRightY = topRightTile.height - topRightTile.origin.y
        fill(rightTile, Rect(
                area.right - rightTile.origin.x,
                area.top + topRightY,
                rightTile.width,
                area.height - topRightY - bottomRightTile.origin.y))
    }

    override fun event(event: Event, executor: TaskExecutor): Boolean {
        when (event) {
            is EventMouseMotion -> {
                return event.position in area
            }
            is EventMouseButton -> {
                return event.position in area
            }
        }
        return false
    }
}

abstract class Control {
    abstract fun render(area: Rect, renderer: Renderer)
}

class Button(val position: Point, private val resources: ResourceContext) : Control() {
    private val tiles = resources.loadTiles("elements")
    private val button = tiles["button"]
    private val buttonHover = tiles["button-hover"]
    private val buttonPressed = tiles["button-pressed"]
    private val buttonDisabled = tiles["button-disabled"]

    override fun render(area: Rect, renderer: Renderer) {
        renderer.draw(button, position.relativeTo(area.origin))
    }
}