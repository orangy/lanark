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
    private fun currentTime() = GLFW.glfwGetTimerValue().toULong()

    actual var start: ULong = currentTime()
        private set

    actual fun reset() {
        start = currentTime()
    }

    actual fun elapsedTicks(): ULong = currentTime() - start

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

    actual fun delay(millis: ULong) {
        Thread.sleep(millis.toLong())
    }
}