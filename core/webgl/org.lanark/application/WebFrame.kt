package org.lanark.application

import org.khronos.webgl.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*

actual class Frame(actual val engine: Engine, val context: WebGLRenderingContext) : ResourceOwner, Managed {
    override fun release() {
        
    }
    actual val size: Size
        get() = Size(context.drawingBufferWidth, context.drawingBufferHeight)
    
    actual val canvasSize: Size
        get() = Size(context.drawingBufferWidth, context.drawingBufferHeight)
    
    actual var minimumSize: Size
        get() = Size(context.drawingBufferWidth, context.drawingBufferHeight)
        set(value) {  }
    actual var maximumSize: Size
        get() = Size(context.drawingBufferWidth, context.drawingBufferHeight)
        set(value) {  }
    actual var title: String
        get() = context.canvas.title
        set(value) { context.canvas.title = value }
    actual var cursor: Cursor?
        get() = null
        set(value) {}
    actual val borders: Margins
        get() = Margins.Empty

    actual fun setBordered(enable: Boolean) {}
    actual fun setResizable(enable: Boolean) {}
    actual fun setWindowMode(mode: FrameMode) {}
    actual fun setIcon(icon: Canvas) {}
    actual fun messageBox(title: String, message: String, icon: MessageBoxIcon) {}
    actual var clip: Rect?
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}

    actual fun clear(color: Color?) {}
    actual fun color(color: Color) {}
    actual fun scale(scale: Float) {}
    actual fun drawLine(from: Point, to: Point) {}
    actual fun present() {}

    actual companion object {
        actual val UndefinedPosition: Int = -1
    }
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
