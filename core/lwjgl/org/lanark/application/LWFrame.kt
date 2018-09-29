package org.lanark.application

import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*

actual class Frame(actual val engine: Engine, val windowHandle: Long) : ResourceOwner, Managed {
    override fun release() {
        glfwDestroyWindow(windowHandle)
    }

    actual val size: Size
        get() {
            val width = IntArray(1)
            val height = IntArray(1)
            glfwGetWindowSize(windowHandle, width, height)
            return Size(width[0], height[0])
        }

    actual val canvasSize: Size
        get() {
            val width = IntArray(1)
            val height = IntArray(1)
            glfwGetFramebufferSize(windowHandle, width, height)
            return Size(width[0], height[0])
        }

    actual var minimumSize: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}
    actual var maximumSize: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}
    actual var brightness: Float
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}

    actual var title: String = ""
        set(value) {
            field = value
            glfwSetWindowTitle(windowHandle, value)
        }

    actual val borders: Margins
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    actual fun setBordered(enable: Boolean) {}
    actual fun setResizable(enable: Boolean) {}
    actual fun setWindowMode(mode: FrameMode) {}
    actual fun setIcon(icon: Canvas) {}
    actual fun messageBox(title: String, message: String, icon: MessageBoxIcon) {}

    actual companion object {
        actual val UndefinedPosition: Int = 0
    }

    actual var clip: Rect?
        get() = Rect.Empty
        set(value) {}

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

    actual fun scale(scale: Float) {}
    actual fun drawLine(from: Point, to: Point) {}
    actual fun present() {
        glfwSwapBuffers(windowHandle) // swap the color buffers
    }
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
