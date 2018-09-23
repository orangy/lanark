package org.lanark.application

import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.system.*

expect class Frame : Managed {
    val engine: Engine
    val size: Size
    var minimumSize: Size
    var maximumSize: Size
    var brightness: Float
    var title: String
    val borders: Margins

    val renderer : Renderer

    fun setBordered(enable: Boolean)
    fun setResizable(enable: Boolean)
    fun setWindowMode(mode: FrameMode)

    fun setIcon(icon: Canvas)

    fun messageBox(title: String, message: String, icon: MessageBoxIcon)

    companion object {
        val UndefinedPosition: Int

        val CreateShown: UInt
        val CreateResizable: UInt
        val CreateFullscreen: UInt
        val CreateHiDPI: UInt
        val CreateOpenGL: UInt
    }
}

enum class FrameMode {
    Windowed,
    FullScreen,
    FullScreenDesktop,
}
