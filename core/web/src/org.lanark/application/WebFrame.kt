package org.lanark.application

import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.media.Image
import org.lanark.resources.*
import org.lanark.system.*
import org.w3c.dom.*

actual class Frame(actual val engine: Engine, val context: CanvasRenderingContext2D, actual val size: Size) : Managed {
    override fun release() {

    }

    actual val identity = context.toString()

    actual val canvasSize: Size
        get() = size

    actual var minimumSize: Size
        get() = size
        set(value) {}
    actual var maximumSize: Size
        get() = size
        set(value) {}
    actual var title: String
        get() = context.canvas.title
        set(value) {
            context.canvas.title = value
        }
    actual var cursor: Cursor?
        get() = null
        set(value) {}
    actual val borders: Margins
        get() = Margins.Empty

    actual fun setBordered(enable: Boolean) {}
    actual fun setResizable(enable: Boolean) {}
    actual fun setWindowMode(mode: FrameMode) {}
    actual fun setIcon(icon: Image) {}
    actual fun messageBox(title: String, message: String, icon: MessageBoxIcon) {}
    actual var clip: Rect?
        get() = null
        set(value) {}

    actual fun clear(color: Color?) {
        if (color != null)
            color(color)
        context.fillRect(0.0, 0.0, size.width.toDouble(), size.height.toDouble())
    }

    actual fun color(color: Color) {
        context.fillStyle = color.toCSS()
    }

    actual fun scale(scale: Float) {}
    actual fun drawLine(from: Point, to: Point) {}
    actual fun present() {}

    actual companion object {
        actual val UndefinedPosition: Int = -1
    }
}

private fun Color.toCSS(): String {
    return "#${red.hex2()}${green.hex2()}${blue.hex2()}"
}

private val hexDigits = "0123456789ABCDEF"
private fun UByte.hex2(): String {
    return "${hexDigits[(this/16u).toInt()]}${hexDigits[(this and 15u).toInt()]}"
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
