package org.lanark.application

import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.system.*
import org.lwjgl.glfw.GLFW.*

actual class Frame(actual val engine: Engine, val windowHandle: Long) : Managed {
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
    
    actual val renderer: Renderer = Renderer()

    actual fun setBordered(enable: Boolean) {}
    actual fun setResizable(enable: Boolean) {}
    actual fun setWindowMode(mode: FrameMode) {}
    actual fun setIcon(icon: Canvas) {}
    actual fun messageBox(title: String, message: String, icon: MessageBoxIcon) {}

    actual companion object {
        actual val UndefinedPosition: Int = 0
        actual val CreateShown: UInt = 0u
        actual val CreateResizable: UInt = 0u
        actual val CreateFullscreen: UInt = 0u
        actual val CreateHiDPI: UInt = 0u
        actual val CreateOpenGL: UInt = 0u
    }

}