package ksdl.ui

import ksdl.geometry.*
import ksdl.rendering.*
import ksdl.resources.*

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

        val topLeftX = topLeftTile.width - topLeftTile.hotPoint.x
        fill(topTile, KRect(
                area.left + topLeftX,
                area.top - topTile.hotPoint.y,
                area.width - topLeftX - topRightTile.hotPoint.x,
                topTile.height))

        val bottomLeftX = bottomLeftTile.width - bottomLeftTile.hotPoint.x
        fill(bottomTile, KRect(
                area.left + bottomLeftX,
                area.bottom - bottomTile.hotPoint.y,
                area.width - bottomLeftX - bottomRightTile.hotPoint.x,
                bottomTile.height))

        val topLeftY = topLeftTile.height - topLeftTile.hotPoint.y
        fill(leftTile, KRect(
                area.left - leftTile.hotPoint.x,
                area.top + topLeftY,
                leftTile.width,
                area.height - topLeftY - bottomLeftTile.hotPoint.y))

        val topRightY = topRightTile.height - topRightTile.hotPoint.y
        fill(rightTile, KRect(
                area.right - rightTile.hotPoint.x,
                area.top + topRightY,
                rightTile.width,
                area.height - topRightY - bottomRightTile.hotPoint.y))
    }
}