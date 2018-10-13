package org.lanark.system

import org.lanark.events.*

class Signal<T>(val tag: String) {
    private val handlers = mutableListOf<(T) -> Unit>()

    fun subscribe(handler: (T) -> Unit) {
        handlers.add(handler)
    }

    fun unsubscribe(handler: (T) -> Unit) {
        handlers.remove(handler)
    }

    fun raise(event: T) {
        for (handler in handlers) {
            handler(event)
        }
    }
}

inline fun <reified TDerivedEvent : Event> Signal<*>.filter(): Signal<TDerivedEvent> {
    val signal = Signal<TDerivedEvent>("$tag.${TDerivedEvent::class.simpleName}")
    subscribe {
        if (it is TDerivedEvent)
            signal.raise(it)
    }
    return signal
}
