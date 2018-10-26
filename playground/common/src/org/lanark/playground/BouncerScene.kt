package org.lanark.playground

import kotlinx.coroutines.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.ui.*
import kotlin.random.*

class BouncerScene(private val application: SceneApplication, resources: ResourceContext) : Scene {
    private val background = resources.texture("welcome/background-image")
    //private val backgroundMusic = resources.loadMusic("welcome/background-music")
    private val normalCursor = resources.cursor("cursors/normal")
    private val hotCursor = resources.cursor("cursors/hot")
    private val tiles = resources.tiles("ui/elements")
    private val item = tiles["button"]

    private val minPosition = 10
    private val maxPosition = application.frame.size.width - item.width - 10

    private val items = List(3) {
        Bouncer(
            item,
            Point(Random.nextInt(minPosition, maxPosition), 5 + item.height * 2 * it),
            minPosition,
            maxPosition,
            Random.nextInt(1, 5),
            normalCursor,
            hotCursor
        )
    }

    override fun activate(frame: Frame) {
        frame.cursor = normalCursor

        application.frame.engine.submit {
            items.forEach {
                launch { it.run() }
            }
        }
        //backgroundMusic.play()
    }

    override fun deactivate(frame: Frame) {
        //backgroundMusic.stop()
    }

    private fun renderBackground(frame: Frame) {
        val scale = (frame.size / background.size).max()
        val destinationRect = Rect(Point(0, 0), background.size * scale)
        frame.draw(background, destinationRect)
    }

    override fun render(frame: Frame) {
        renderBackground(frame)
        items.forEach { it.render(frame) }
    }

    override fun event(frame: Frame, event: Event): Boolean {
        val handled = items.any { it.handle(frame, event) }
        if (!handled)
            frame.cursor = normalCursor
        return handled
    }
}