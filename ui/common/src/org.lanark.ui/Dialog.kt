package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*

class Dialog(val area: Rect, private val resources: ResourceContext, val controls: List<Control>) : UIScene() {
    private val background = resources.texture("background")
    private val tiles = resources.tiles("elements")

    private val topLeftTile = tiles["border-top-left"]
    private val topRightTile = tiles["border-top-right"]
    private val bottomLeftTile = tiles["border-bottom-left"]
    private val bottomRightTile = tiles["border-bottom-right"]
    private val topTile = tiles["border-top"]
    private val rightTile = tiles["border-right"]
    private val leftTile = tiles["border-left"]
    private val bottomTile = tiles["border-bottom"]

    override fun render(frame: Frame) {
        frame.renderFrame()
        frame.renderControls()
    }

    private fun Frame.renderControls() {
        clip(area) {
            controls.forEach {
                it.render(this, area)
            }
        }
    }

    private fun Frame.renderFrame() {
        fill(background, area)
        draw(topLeftTile, Point(area.left, area.top))
        draw(topRightTile, Point(area.right, area.top))
        draw(bottomLeftTile, Point(area.left, area.bottom))
        draw(bottomRightTile, Point(area.right, area.bottom))

        drawTopBorder()
        drawBottomBorder()
        drawLeftBorder()
        drawRightBorder()
    }

    private fun Frame.drawRightBorder() {
        val topRightY = topRightTile.height - topRightTile.origin.y
        fill(
            rightTile, Rect(
                area.right - rightTile.origin.x,
                area.top + topRightY,
                rightTile.width,
                area.height - topRightY - bottomRightTile.origin.y
            )
        )
    }

    private fun Frame.drawLeftBorder() {
        val topLeftY = topLeftTile.height - topLeftTile.origin.y
        fill(
            leftTile, Rect(
                area.left - leftTile.origin.x,
                area.top + topLeftY,
                leftTile.width,
                area.height - topLeftY - bottomLeftTile.origin.y
            )
        )
    }

    private fun Frame.drawBottomBorder() {
        val bottomLeftX = bottomLeftTile.width - bottomLeftTile.origin.x
        fill(
            bottomTile, Rect(
                area.left + bottomLeftX,
                area.bottom - bottomTile.origin.y,
                area.width - bottomLeftX - bottomRightTile.origin.x,
                bottomTile.height
            )
        )
    }

    private fun Frame.drawTopBorder() {
        val topLeftX = topLeftTile.width - topLeftTile.origin.x
        fill(
            topTile, Rect(
                area.left + topLeftX,
                area.top - topTile.origin.y,
                area.width - topLeftX - topRightTile.origin.x,
                topTile.height
            )
        )
    }

    override fun event(frame: Frame, event: Event): Boolean {
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

