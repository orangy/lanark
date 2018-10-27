package org.lanark.playground

import org.lanark.application.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.playground.hex.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lanark.ui.*

private val title = "Lanark Demo"

suspend fun Engine.game(frame: Frame) {
    val context = gameAssets.load(frame) { /*progress*/ }
    val uiResources = context.loadScope("ui")
    
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
    val dialog = Dialog(
        Rect(140, 140, 412, 234), uiResources, listOf(
            Button(Point(20, 20), uiResources),
            Button(Point(20, 80), uiResources)
        )
    )
    val bouncerScene = BouncerScene(application, dialog, context)
    val hexScene = HexScene(context)
    val welcome = WelcomeScene(application, bouncerScene, context)

    application.start(bouncerScene)

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

