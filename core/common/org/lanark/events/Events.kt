package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*

expect class Events(engine: Engine) {
    val all: Signal<Event>
    val window: Signal<EventWindow>
    val application: Signal<EventApp>
    val keyboard: Signal<EventKey>
    val mouse: Signal<EventMouse>
    
    fun poll()
    
    companion object {
        val LogCategory: LoggerCategory
    }
}

fun Logger.event(message: () -> String) = log(Events.LogCategory, message)




