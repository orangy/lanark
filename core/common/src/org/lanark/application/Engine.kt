package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.system.*

expect class Engine(configure: EngineConfiguration.() -> Unit) {
    /**
     * Instance of a [Logger] for reporting diagnostic information. 
     */
    val logger: Logger

    /**
     * Fires when an [Event] is registered for the application.
     */
    val events: Signal<Event>

    fun createFrame(
        title: String,
        width: Int,
        height: Int,
        x: Int = Frame.UndefinedPosition,
        y: Int = Frame.UndefinedPosition,
        flags: FrameFlag = FrameFlag.CreateVisible
    ): Frame

    /**
     * Destroys the engine.
     */
    fun destroy()

    /**
     * Fires before the coroutines are run inside a game loop.
     */
    val before: Signal<Unit>
    
    /**
     * Fires after coroutines have been run inside a game loop.
     */
    val after: Signal<Unit>

    /**
     * Starts the [main] game function in the appropriate execution context.
     * Main function should load initial resources, set up an application and eventually call [loop] 
     * function to start a game loop. 
     */
    fun run(main: suspend Engine.() -> Unit)
    
    /**
     * Runs the game loop. Should be called from within the main function
     */
    suspend fun loop()

    /**
     * Suspends until the next game tick.
     * @return time in seconds passed from suspension to resume.
     */
    suspend fun nextTick(): Float

    /**
     * Cancels all the running coroutines and stops the loop.
     * At this point [loop] function returns to the caller.
     */
    fun exitLoop()

    /**
     * Creates a scope for running coroutines.
     */
    fun createCoroutineScope(): CoroutineScope

    companion object {
        val LogCategory: LoggerCategory
    }
}

/**
 * Exception thrown when an [Engine] error occurs.
 */
class EngineException(message: String) : Exception(message)

/**
 * Logs a [message] in the [Engine.LogCategory] category.
 */
fun Logger.engine(message: () -> String) = log(Engine.LogCategory, message)
fun Logger.engine(message: String) = log(Engine.LogCategory, message)

