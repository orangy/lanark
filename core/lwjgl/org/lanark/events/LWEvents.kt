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
        glfwSetKeyCallback(frame.windowHandle) { windowId, key, scancode, action, mods ->
            val event = when (action) {
                GLFW_PRESS -> EventKeyDown(frame, key, scancode.toUInt(), false)
                GLFW_REPEAT -> EventKeyDown(frame, key, scancode.toUInt(), false)
                GLFW_RELEASE -> EventKeyUp(frame, key, scancode.toUInt())
                else -> throw EngineException("Unknown action $action")
            }
            all.raise(event)
        }

        glfwSetMouseButtonCallback(frame.windowHandle) { window, button, action, mods ->
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
            all.raise(EventMouseButtonDown(frame, mouseButton, x[0].toInt(), y[0].toInt()))
        }
        
        glfwSetCursorPosCallback(frame.windowHandle) { window, xpos, ypos ->
            all.raise(EventMouseMotion(frame, xpos.toInt(), ypos.toInt()))
        }

        glfwSetWindowCloseCallback(frame.windowHandle) {
            all.raise(EventWindowClose(frame))
        }

        glfwSetWindowSizeCallback(frame.windowHandle) { windowId, width, height ->
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