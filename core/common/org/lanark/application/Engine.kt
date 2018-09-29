package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*

expect class Engine(configure: EngineConfiguration.() -> Unit) {
    fun quit()
    
    val logger: Logger
    val events: Events
    val executor: TaskExecutor
    
    fun sleep(millis: UInt)
    fun setScreenSaver(enabled: Boolean)
    
    var activeCursor: Cursor?

    fun createFrame(title: String,
                    width: Int,
                    height: Int,
                    x: Int = Frame.UndefinedPosition,
                    y: Int = Frame.UndefinedPosition,
                    flags: FrameFlag = FrameFlag.CreateVisible
    ): Frame

    fun createCursor(canvas: Canvas, hotX: Int, hotY: Int): Cursor
    fun createCursor(systemCursor: SystemCursor): Cursor
    fun createCanvas(size: Size, bitsPerPixel: Int): Canvas 

    fun loadCanvas(path: String, fileSystem: FileSystem): Canvas
    fun loadMusic(path: String, fileSystem: FileSystem): Music
    fun loadSound(path: String, fileSystem: FileSystem): Sound
    fun loadVideo(path: String, fileSystem: FileSystem): Video
}

class EngineException(message: String) : Exception(message)
