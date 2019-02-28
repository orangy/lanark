package org.lanark.playground

import kotlinx.coroutines.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.math.*
import org.lanark.resources.*
import org.lanark.ui.*
import kotlin.random.*

class BouncerScene(
    private val application: SceneApplication,
    private val nextScene: Scene,
    resources: ResourceContext
) : Scene {

    private val coroutineScope = application.frame.engine.createCoroutineScope()

    private val background = resources.texture("welcome/background-image")
    private val normalCursor = resources.cursor("cursors/normal")
    private val hotCursor = resources.cursor("cursors/hot")
    private val tiles = resources.tiles("ui/elements")
    private val item = tiles["button"]

    private val minPosition = 10f
    private val maxPosition = (application.frame.size.width - item.width - 10).toFloat()

    private val bouncers = List(6) {
        Bouncer(
            item,
            vectorOf(Random.nextFloat(minPosition, maxPosition), (5 + item.height * 1.5 * it).toFloat()),
            minPosition,
            maxPosition,
            Random.nextFloat(1f, 100f),
            normalCursor,
            hotCursor
        )
    }

    override fun activate(frame: Frame) {
        frame.cursor = normalCursor

        application.frame.engine.logger.engine("Launching bouncers")
        bouncers.forEach { bouncer ->
            coroutineScope.launch {
                bouncer.run(frame.engine)
            }
        }
    }

    override fun deactivate(frame: Frame) {
        coroutineScope.coroutineContext.cancel()
    }

    private fun renderBackground(frame: Frame) {
        val scale = (frame.size / background.size).max()
        val destinationRect = Rect(Point(0, 0), background.size * scale)
        frame.draw(background, destinationRect)
    }

    override fun render(frame: Frame) {
        renderBackground(frame)
        bouncers.forEach { it.render(frame) }
    }

    override fun event(frame: Frame, event: Event): Boolean {
        if (event is EventMouseButtonDown) {
            application.scene = nextScene
        }

        val handled = bouncers.any { it.handle(frame, event) }
        if (!handled)
            frame.cursor = normalCursor
        return handled
    }

    override fun toString() = "BouncerScene"
}