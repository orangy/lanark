package org.lanark.application

import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.system.*

actual class Frame : Managed {
    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual val engine: Engine
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val size: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual var minimumSize: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}
    actual var maximumSize: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}
    actual var brightness: Float
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}
    actual var title: String
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}
    actual val borders: Margins
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val renderer: Renderer
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    actual fun setBordered(enable: Boolean) {}
    actual fun setResizable(enable: Boolean) {}
    actual fun setWindowMode(mode: FrameMode) {}
    actual fun setIcon(icon: Canvas) {}
    actual fun messageBox(title: String, message: String, icon: MessageBoxIcon) {}

    actual companion object {
        actual val UndefinedPosition: Int
            get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        actual val CreateShown: UInt
            get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        actual val CreateResizable: UInt
            get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        actual val CreateFullscreen: UInt
            get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        actual val CreateHiDPI: UInt
            get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        actual val CreateOpenGL: UInt
            get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

}