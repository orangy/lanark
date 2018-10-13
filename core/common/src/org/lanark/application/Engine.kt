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
    val executor: Executor
    
    fun sleep(millis: UInt)
    fun setScreenSaver(enabled: Boolean)
    
    fun createFrame(title: String,
                    width: Int,
                    height: Int,
                    x: Int = Frame.UndefinedPosition,
                    y: Int = Frame.UndefinedPosition,
                    flags: FrameFlag = FrameFlag.CreateVisible
    ): Frame

    fun postQuitEvent()
}

class EngineException(message: String) : Exception(message)
