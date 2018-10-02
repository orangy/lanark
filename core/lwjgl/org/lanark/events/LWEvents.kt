package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import org.lwjgl.glfw.GLFW.*

actual class Events actual constructor(engine: Engine) {
    private val clock = Clock()

    actual val all: Signal<Event> = Signal("Event")
    actual val window: Signal<EventWindow> = all.filter()
    actual val application: Signal<EventApp> = all.filter()
    actual val keyboard: Signal<EventKey> = all.filter()
    actual val mouse: Signal<EventMouse> = all.filter()

    internal fun attachEvents(frame: Frame) {
        glfwSetKeyCallback(frame.windowHandle) { windowId, key, scancode, action, mods ->
            val timestamp = clock.elapsedTicks()
            val event = when (action) {
                GLFW_PRESS -> EventKeyDown(timestamp, frame, key, scancode.toUInt(), false)
                GLFW_REPEAT -> EventKeyDown(timestamp, frame, key, scancode.toUInt(), false)
                GLFW_RELEASE -> EventKeyUp(timestamp, frame, key, scancode.toUInt())
                else -> throw EngineException("Unknown action $action")
            }
            all.raise(event)
        }

        glfwSetScrollCallback(frame.windowHandle) { window, dx, dy ->
            val timestamp = clock.elapsedTicks()
            all.raise(EventMouseScroll(timestamp, frame, dx.toInt(), dy.toInt()))
        }

        glfwSetMouseButtonCallback(frame.windowHandle) { window, button, action, mods ->
            val timestamp = clock.elapsedTicks()
            val x = DoubleArray(1)
            val y = DoubleArray(1)
            glfwGetCursorPos(frame.windowHandle, x, y)
            val mouseButton = when (button) {
                GLFW_MOUSE_BUTTON_LEFT -> MouseButton.Left
                GLFW_MOUSE_BUTTON_MIDDLE -> MouseButton.Middle
                GLFW_MOUSE_BUTTON_RIGHT -> MouseButton.Right
                GLFW_MOUSE_BUTTON_4 -> MouseButton.X1
                GLFW_MOUSE_BUTTON_5 -> MouseButton.X2
                else -> return@glfwSetMouseButtonCallback // unknown button, skip the click
            }
            val clicks = 1u
            val posX = x[0].toInt()
            val posY = y[0].toInt()
            val event = when (action) {
                GLFW_PRESS -> EventMouseButtonDown(timestamp, frame, mouseButton, posX, posY, clicks)
                GLFW_RELEASE -> EventMouseButtonUp(timestamp, frame, mouseButton, posX, posY, clicks)
                else -> throw EngineException("Unrecognized mouse action")
            }
            all.raise(event)
        }

        glfwSetCursorPosCallback(frame.windowHandle) { window, xpos, ypos ->
            val timestamp = clock.elapsedTicks()
            all.raise(EventMouseMotion(timestamp, frame, xpos.toInt(), ypos.toInt(), 0, 0))
        }

        glfwSetWindowCloseCallback(frame.windowHandle) {
            val timestamp = clock.elapsedTicks()
            all.raise(EventWindowClose(timestamp, frame))
        }

        glfwSetWindowSizeCallback(frame.windowHandle) { windowId, width, height ->
            val timestamp = clock.elapsedTicks()
            all.raise(EventWindowSizeChanged(timestamp, frame, width, height))
        }
    }

    actual fun poll() {
        glfwPollEvents()
    }

    actual companion object {
        actual val LogCategory = LoggerCategory("Events")
    }
}

actual fun getScanCodeName(scanCode: UInt): String? {
    return null
}