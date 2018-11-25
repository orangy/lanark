package org.lanark.application

import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.media.*
import org.lanark.resources.*
import org.lanark.system.*

expect class Frame : Managed {
    val identity : String
    val engine: Engine
    val size: Size
    val canvasSize: Size
    var minimumSize: Size
    var maximumSize: Size
    var title: String
    var cursor: Cursor?
    val borders: Margins

    fun setBordered(enable: Boolean)
    fun setResizable(enable: Boolean)
    fun setWindowMode(mode: FrameMode)

    fun setIcon(icon: Image)

    fun messageBox(title: String, message: String, icon: MessageBoxIcon)

    var clip: Rect?
    fun clear(color: Color? = null)
    fun color(color: Color, body: () -> Unit)
    fun scale(scale: Float)

    fun drawLine(from: Point, to: Point)

    fun present()

    companion object {
        val UndefinedPosition: Int
    }
}

expect class FrameFlag {
    operator fun plus(flag: FrameFlag): FrameFlag

    operator fun contains(flag: FrameFlag): Boolean

    companion object {
        val CreateVisible: FrameFlag
        val CreateResizable: FrameFlag
        val CreateFullscreen: FrameFlag
        val CreateFullscreenDesktop: FrameFlag
        val CreateHiDPI: FrameFlag
    }
}

enum class FrameMode {
    Windowed,
    FullScreen,
    FullScreenDesktop,
}

inline fun Frame.clip(rectangle: Rect, body: () -> Unit) {
    val old = clip
    try {
        clip = rectangle
        body()
    } finally {
        clip = old
    }
}

inline val Frame.rectangle get() = Rect(Point.Zero, canvasSize)
inline val Frame.width get() = size.width
inline val Frame.height get() = size.height