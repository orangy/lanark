package ksdl.diagnostics

// TODO: May be use SDL_Log: https://wiki.libsdl.org/SDL_Log

interface KLogger {
    fun log(category: KLogCategory, message: String) = log(category) { message }
    fun log(category: KLogCategory, message: () -> String)

    fun switch(category: KLogCategory, enable: Boolean)
}

class KLogCategory(val name: String) {
    companion object {
        val System = KLogCategory("System")
        val Trace = KLogCategory("Trace")
        val Info = KLogCategory("Info")
        val Warn = KLogCategory("Warn")
        val Error = KLogCategory("Error")
    }
}

object KLoggerNone : KLogger {
    override fun log(category: KLogCategory, message: () -> String) {}
    override fun switch(category: KLogCategory, enable: Boolean) {}
}

fun KLogger.system(message: String) = log(KLogCategory.System, message)
fun KLogger.trace(message: String) = log(KLogCategory.Trace, message)
fun KLogger.info(message: String) = log(KLogCategory.Info, message)
fun KLogger.warn(message: String) = log(KLogCategory.Warn, message)
fun KLogger.error(message: String) = log(KLogCategory.Error, message)