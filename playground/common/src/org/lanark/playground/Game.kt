package org.lanark.playground

import kotlinx.coroutines.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.playground.hex.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lanark.ui.*

private val title = "Lanark Demo"

fun game(frame: Frame) {
    val engine = frame.engine

    val context = gameAssets.load(frame) { /*progress*/ }
    val uiResources = context.loadScope("ui")
    
    frame.setIcon(uiResources.loadImage("logo/icon"))

    var frames = 0
    val clock = Clock()
    engine.executor.after.subscribe {
        frames++
        val seconds = clock.elapsedMillis()
        if (seconds > 1000u) {
            clock.reset()
            frame.title = "$title / FPS: $frames"
            frames = 0
        }

    }
    engine.logger.switch(Events.LogCategory, false)

    engine.events.window.filter<EventWindowClose>().subscribe {
        engine.postQuitEvent()
    }

    val application = SceneApplication(frame)
    val dialog = Dialog(
        Rect(140, 140, 412, 234), uiResources, listOf(
            Button(Point(20, 20), uiResources),
            Button(Point(20, 80), uiResources)
        )
    )
    val shieldScene = BouncerScene(application, context)
    val hexScene = HexScene(context)
    val welcome = WelcomeScene(application, shieldScene, context)

    application.scene = shieldScene

    try {
        coroutineLoop {
            try {
                application.run()
            } catch (e: Throwable) {
                println("Main: $e")
                throw e
            }
            uiResources.release()
            frame.release()
            engine.quit()
        }
    } catch (e: CancellationException) {
        engine.logger.error(e.toString())
    }
}

