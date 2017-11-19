package ksdl.ui

import ksdl.events.*
import ksdl.geometry.*
import ksdl.rendering.*
import ksdl.resources.*
import ksdl.system.*

class KDialog(val area: KRect, private val resources: KResourceContext) : UIScene() {
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

    override fun render(renderer: KRenderer) {
        renderer.renderFrame()
    }

    private fun KRenderer.renderFrame() {
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