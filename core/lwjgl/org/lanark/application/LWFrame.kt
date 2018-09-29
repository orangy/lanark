package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL14.*

actual class Frame(actual val engine: Engine, val id: Long) : ResourceOwner, Managed {

    override fun release() {
        engine.unregisterFrame(id, this)
        glfwDestroyWindow(id)
        engine.logger.system("Released frame #$id")
    }

    actual val size: Size
        get() {
            val width = IntArray(1)
            val height = IntArray(1)
            glfwGetWindowSize(id, width, height)
            return Size(width[0], height[0])
        }

    actual val canvasSize: Size
        get() {
            val width = IntArray(1)
            val height = IntArray(1)
            glfwGetFramebufferSize(id, width, height)
            return Size(width[0], height[0])
        }

    actual var minimumSize: Size = Size.Empty
        set(value) {
            field = value
            updateLimits()
        }

    actual var maximumSize: Size = Size.Empty
        set(value) {
            field = value
            updateLimits()
        }

    private fun updateLimits() = when  {
        minimumSize == Size.Empty && maximumSize == Size.Empty -> 
            glfwSetWindowSizeLimits(id, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE)
        minimumSize == Size.Empty ->
            glfwSetWindowSizeLimits(id, GLFW_DONT_CARE, GLFW_DONT_CARE, maximumSize.width, maximumSize.height)
        maximumSize == Size.Empty ->
            glfwSetWindowSizeLimits(id, minimumSize.width, minimumSize.height, GLFW_DONT_CARE, GLFW_DONT_CARE)
        else -> 
            glfwSetWindowSizeLimits(id, minimumSize.width, minimumSize.height, maximumSize.width, maximumSize.height)
    }
    
    actual var title: String = ""
        set(value) {
            field = value
            glfwSetWindowTitle(id, value)
        }

    actual val borders: Margins
        get() = TODO()

    actual fun setBordered(enable: Boolean) {}
    actual fun setResizable(enable: Boolean) {}
    actual fun setWindowMode(mode: FrameMode) {}
    actual fun setIcon(icon: Canvas) {}
    actual fun messageBox(title: String, message: String, icon: MessageBoxIcon) {}

    actual companion object {
        actual val UndefinedPosition: Int = 0
    }

    actual var clip: Rect? = null
        set(value) {
            field = value
            if (value == null) {
                glDisable(GL_SCISSOR_TEST)
            } else {
                glEnable(GL_SCISSOR_TEST)
                glScissor(value.x, value.y, value.width, value.height)
            }
        }

    actual fun clear(color: Color?) {
        if (color != null)
            color(color)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer
    }

    actual fun color(color: Color) {
        glClearColor(
            color.red.toInt().toFloat() / 255.0f,
            color.green.toInt().toFloat() / 255.0f,
            color.blue.toInt().toFloat() / 255.0f,
            color.alpha.toInt().toFloat() / 255.0f
        )
    }

    actual fun scale(scale: Float) {
        TODO()
    }

    actual fun drawLine(from: Point, to: Point) {
        TODO()
    }

    actual fun present() {
        glfwSwapBuffers(id) // swap the color buffers
    }

    override fun toString() = "Frame #$id"
}

actual class FrameFlag(val value: Int) {
    actual companion object {
        actual val CreateVisible = FrameFlag(GLFW_VISIBLE)
        actual val CreateResizable = FrameFlag(GLFW_RESIZABLE)
        actual val CreateFullscreen = FrameFlag(0)
        actual val CreateHiDPI = FrameFlag(0)
    }

    actual operator fun plus(flag: FrameFlag) = FrameFlag(value or flag.value)
    actual operator fun contains(flag: FrameFlag) = (flag.value and value) != 0
}
