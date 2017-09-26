package ksdl.system

class KEventSource<T>(val tag: String) {
    private val handlers = mutableListOf<(T) -> Unit>()

    fun subscribe(handler: (T) -> Unit) {
        handlers.add(handler)
    }

    fun raise(event: T) {
        handlers.forEach {
            try {
                it(event)
            } catch (e: Throwable) {
                logger.error("Error handling $tag event $event: $e")
            }
        }
    }
}