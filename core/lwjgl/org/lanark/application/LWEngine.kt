package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual fun quit() {}
    actual val logger: Logger
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    actual fun sleep(millis: UInt) {}
    actual fun setScreenSaver(enabled: Boolean) {}
    actual var activeCursor: Cursor?
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}

    actual fun createFrame(
        title: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        windowFlags: UInt
    ): Frame {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createCursor(canvas: Canvas, hotX: Int, hotY: Int): Cursor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createCursor(systemCursor: SystemCursor): Cursor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createCanvas(size: Size, bitsPerPixel: Int): Canvas {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadCanvas(path: String, fileSystem: FileSystem): Canvas {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadMusic(path: String, fileSystem: FileSystem): Music {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadSound(path: String, fileSystem: FileSystem): Sound {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadVideo(path: String, fileSystem: FileSystem): Video {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}