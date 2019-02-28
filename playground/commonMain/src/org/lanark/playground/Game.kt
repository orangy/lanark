package org.lanark.playground

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lanark.ui.*

private val title = "Lanark Demo"

suspend fun Engine.game(frame: Frame) {
    val assets = gameAssets.load(frame) { /*progress*/ }
    val uiResources = assets.loadScope("ui")

    frame.setIcon(uiResources.loadImage("logo/icon"))

    var fpsCounter = 0
    val fpsClock = Clock()
    after.subscribe {
        fpsCounter++
        val seconds = fpsClock.elapsedMillis()
        if (seconds > 1000u) {
            fpsClock.reset()
            frame.title = "$title / FPS: $fpsCounter"
            fpsCounter = 0
        }
    }

    val application = SceneApplication(frame)
    val padding = Size(40, 40)
    val dialogSize = frame.size - padding
    val dialog = dialog(Rect(Point(padding.width / 2, padding.height / 2), dialogSize), uiResources) {
        fontName = "font"
        text(10, 10, 300, 70) {
            wrap = WordWrap.Words
            color = Color.LightGreen
            text = "Lanark"
        }
        text(10, 40, 600, 70) {
            color = Color.LightBlue
            text = "Oh really?! That's so fascinating! Awesome, LOL! :)"
        }
        button(10, 90) {
            text = "Start"
        }
        button(10, 150) {
            text = "Close"
        }
    }

/*
    val bouncerScene = BouncerScene(application, dialog, assets)
    val hexScene = HexScene(assets)
*/

    val scroller = ScrollerScene(application, assets.loadScope("scroller"))
    val welcome = WelcomeScene(application, scroller, assets)

    application.start(dialog)

    events.filter<EventAppQuit>().subscribe {
        exitLoop()
    }
    events.filter<EventWindowClose>().subscribe {
        exitLoop()
    }

    loop()

    application.stop()
    uiResources.release()
    frame.release()
}

