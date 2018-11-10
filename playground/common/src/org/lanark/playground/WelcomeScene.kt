package org.lanark.playground

import kotlinx.coroutines.*
import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.ui.*

class WelcomeScene(private val application: SceneApplication,
                   private val nextScene: Scene,
                   resources: ResourceContext
) : Scene {
    private val logo = resources.texture("logo/logo")
    private val coroutineScope = application.frame.engine.createCoroutineScope()
    private var timePassed = 0f
    private val waitTime = 5f

    override fun activate(frame: Frame) {
        coroutineScope.launch {
            while (true) {
                timePassed += frame.engine.nextTick()
                if (timePassed >= waitTime)
                    advance()
            }
        }
    }

    override fun deactivate(frame: Frame) {
        coroutineScope.coroutineContext.cancel()
    }

    override fun render(frame: Frame) {
        frame.clear(Color.ALMOST_BLACK)
        val offset = frame.size / 2.0 - logo.size / 4.0
        frame.draw(logo, Point(offset.width, offset.height), logo.size / 2.0)
    }

    override fun event(frame: Frame, event: Event): Boolean {
        if (event is EventMouseButtonDown) {
            advance()
            return true
        }
        return false
    }

    private fun advance() {
        application.scene = nextScene
    }
}