package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*

expect class Engine(configure: EngineConfiguration.() -> Unit) {
    val logger: Logger
    
    fun createFrame(title: String,
                    width: Int,
                    height: Int,
                    x: Int = Frame.UndefinedPosition,
                    y: Int = Frame.UndefinedPosition,
                    flags: FrameFlag = FrameFlag.CreateVisible
    ): Frame

    fun quit()

    val events: Signal<Event>
    fun pollEvents()
    fun postQuitEvent()

    fun submit(task: suspend CoroutineScope.() -> Unit)

    suspend fun run()
    val before: Signal<Unit>
    val after: Signal<Unit>
    fun stop()

    companion object {
        val EventsLogCategory: LoggerCategory
    }
}

class EngineException(message: String) : Exception(message)

fun Logger.event(message: () -> String) = log(Engine.EventsLogCategory, message)

expect suspend fun nextTick(): Double 