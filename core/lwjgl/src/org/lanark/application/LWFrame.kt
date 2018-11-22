package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.media.*
import org.lanark.resources.*
import org.lanark.system.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.system.MemoryUtil.*

actual class Frame(actual val engine: Engine, val windowHandle: Long) : Managed {

    actual val identity = windowHandle.toString()
    
    override fun release() {
        engine.unregisterFrame(windowHandle, this)
        glfwDestroyWindow(windowHandle)
        engine.logger.system("Released frame [$windowHandle]")
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

    actual var cursor: Cursor? = null
        set(value) {
            if (field == value)
                return
            field = value
            glfwSetCursor(windowHandle, field?.cursorHandle ?: NULL)
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
            glfwSetWindowSizeLimits(windowHandle, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE)
        minimumSize == Size.Empty ->
            glfwSetWindowSizeLimits(windowHandle, GLFW_DONT_CARE, GLFW_DONT_CARE, maximumSize.width, maximumSize.height)
        maximumSize == Size.Empty ->
            glfwSetWindowSizeLimits(windowHandle, minimumSize.width, minimumSize.height, GLFW_DONT_CARE, GLFW_DONT_CARE)
        else -> 
            glfwSetWindowSizeLimits(windowHandle, minimumSize.width, minimumSize.height, maximumSize.width, maximumSize.height)
    }
    
    actual var title: String = ""
        set(value) {
            field = value
            glfwSetWindowTitle(windowHandle, value)
        }

    actual val borders: Margins
        get() = TODO()

    actual fun setBordered(enable: Boolean) {}
    actual fun setResizable(enable: Boolean) {}
    actual fun setWindowMode(mode: FrameMode) {}
    
    actual fun setIcon(icon: Image) {
        GLFWImage.calloc(1).use { buffer ->
            buffer.put(icon.imageBuffer)
            glfwSetWindowIcon(windowHandle, buffer)
        }
    }
    
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
                // Scissor is in canvas coordinates
                val scale = canvasSize / size
                val left = (value.left * scale.horizontal).toInt()
                val bottom = ((size.height - value.y - value.height) * scale.vertical).toInt()
                glScissor(left, bottom, (value.width * scale.horizontal).toInt(), (value.height * scale.vertical).toInt())
                glEnable(GL_SCISSOR_TEST)
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
        
    }

    actual fun drawLine(from: Point, to: Point) {
        TODO()
    }

    actual fun present() {
        glfwSwapBuffers(windowHandle) // swap the color buffers
    }

    override fun toString() = "Frame [$windowHandle] Size:$size, Canvas:$canvasSize"
}

actual class FrameFlag(val value: Int) {
    actual companion object {
        actual val CreateVisible = FrameFlag(1)
        actual val CreateResizable = FrameFlag(2)
        actual val CreateFullscreen = FrameFlag(4)
        actual val CreateFullscreenDesktop = FrameFlag(8)
        actual val CreateHiDPI = FrameFlag(16)
    }

    actual operator fun plus(flag: FrameFlag) = FrameFlag(value or flag.value)
    actual operator fun contains(flag: FrameFlag) = (flag.value and value) != 0
}
