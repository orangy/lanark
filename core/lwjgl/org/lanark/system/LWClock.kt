package org.lanark.system

import org.lanark.application.*
import org.lwjgl.glfw.*

actual class Clock actual constructor() {
    init {
        GLFW.glfwInit()
        GLFW.glfwSetErrorCallback { code, str ->
            throw EngineException(GLFWErrorCallback.getDescription(str))
        }
    }
    private val frequency = GLFW.glfwGetTimerFrequency().toULong()

    actual var start: ULong = GLFW.glfwGetTimerValue().toULong()
        private set

    actual fun reset() {
        start = GLFW.glfwGetTimerValue().toULong()
    }

    actual fun delay(millis: ULong) {
        Thread.sleep(millis.toLong())
    }
    
    actual fun elapsedTicks(): ULong = GLFW.glfwGetTimerValue().toULong() - start

    actual fun elapsedMillis(): ULong {
        val elapsed = elapsedTicks() * 1000u
        return (elapsed / frequency)
    }

    actual fun elapsedMicros(): ULong {
        val elapsed = elapsedTicks() * 1000_000u
        return (elapsed / frequency)
    }

    actual fun elapsedSeconds(): ULong {
        return (elapsedTicks() / frequency)
    }
}