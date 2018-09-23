package org.lanark.ui

import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.rendering.*
import org.lanark.resources.*
import org.lanark.system.*

class KDialog(val area: KRect, private val resources: ResourceContext, val controls: List<KControl>) : UIScene() {
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
        draw(topLeftTile, KPoint(area.left, area.top))
        draw(topRightTile, KPoint(area.right, area.top))
        draw(bottomLeftTile, KPoint(area.left, area.bottom))
        draw(bottomRightTile, KPoint(area.right, area.bottom))

        val topLeftX = topLeftTile.width - topLeftTile.origin.x
        fill(topTile, KRect(
                area.left + topLeftX,
                area.top - topTile.origin.y,
                area.width - topLeftX - topRightTile.origin.x,
                topTile.height))

        val bottomLeftX = bottomLeftTile.width - bottomLeftTile.origin.x
        fill(bottomTile, KRect(
                area.left + bottomLeftX,
                area.bottom - bottomTile.origin.y,
                area.width - bottomLeftX - bottomRightTile.origin.x,
                bottomTile.height))

        val topLeftY = topLeftTile.height - topLeftTile.origin.y
        fill(leftTile, KRect(
                area.left - leftTile.origin.x,
                area.top + topLeftY,
                leftTile.width,
                area.height - topLeftY - bottomLeftTile.origin.y))

        val topRightY = topRightTile.height - topRightTile.origin.y
        fill(rightTile, KRect(
                area.right - rightTile.origin.x,
                area.top + topRightY,
                rightTile.width,
                area.height - topRightY - bottomRightTile.origin.y))
    }

    override fun event(event: KEvent, executor: KTaskExecutor): Boolean {
        when (event) {
            is KEventMouseMotion -> {
                return event.position in area
            }
            is KEventMouseButton -> {
                return event.position in area
            }
        }
        return false
    }
}

abstract class KControl {
    abstract fun render(area: KRect, renderer: Renderer)
}

class KButton(val position: KPoint, private val resources: ResourceContext) : KControl() {
    private val tiles = resources.loadTiles("elements")
    private val button = tiles["button"]
    private val buttonHover = tiles["button-hover"]
    private val buttonPressed = tiles["button-pressed"]
    private val buttonDisabled = tiles["button-disabled"]

    override fun render(area: KRect, renderer: Renderer) {
        renderer.draw(button, position.relativeTo(area.origin))
    }
}