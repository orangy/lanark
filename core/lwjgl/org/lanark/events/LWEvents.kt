package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import org.lwjgl.glfw.GLFW.*

actual class Events actual constructor(engine: Engine) {
    actual val all: Signal<Event> = Signal("Event")
    actual val window: Signal<EventWindow> = all.filter()
    actual val application: Signal<EventApp> = all.filter()
    actual val keyboard: Signal<EventKey> = all.filter()
    actual val mouse: Signal<EventMouse> = all.filter()

    internal fun attachEvents(frame: Frame) {
        glfwSetKeyCallback(frame.id) { windowId, key, scancode, action, mods ->
            val event = when (action) {
                GLFW_PRESS -> EventKeyDown(frame, key, scancode.toUInt(), false)
                GLFW_REPEAT -> EventKeyDown(frame, key, scancode.toUInt(), false)
                GLFW_RELEASE -> EventKeyUp(frame, key, scancode.toUInt())
                else -> throw EngineException("Unknown action $action")
            }
            all.raise(event)
        }
        
        glfwSetWindowCloseCallback(frame.id) {
            all.raise(EventWindowClose(frame))
        }

        glfwSetWindowSizeCallback(frame.id) { windowId, width, height ->
            all.raise(EventWindowSizeChanged(frame, width, height))
        }
    }

    actual fun poll() {
        glfwPollEvents()
    }

    actual companion object {
        actual val LogCategory = LoggerCategory("Events")
    }
}