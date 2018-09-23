package org.lanark.diagnostics

interface Logger {
    fun log(category: LoggerCategory, message: String)
    fun switch(category: LoggerCategory, enable: Boolean)
    fun isEnabled(category: LoggerCategory): Boolean
}

class LoggerCategory(val name: String) {
    companion object {
        val System = LoggerCategory("System")
        val Trace = LoggerCategory("Trace")
        val Info = LoggerCategory("Info")
        val Warn = LoggerCategory("Warn")
        val Error = LoggerCategory("Error")
    }
}

object LoggerNone : Logger {
    override fun isEnabled(category: LoggerCategory): Boolean = false
    override fun log(category: LoggerCategory, message: String) {}
    override fun switch(category: LoggerCategory, enable: Boolean) {}
}

inline fun Logger.log(category: LoggerCategory, message: () -> String) {
    if (isEnabled(category)) {
        log(category, message())
    }
}

fun Logger.system(message: String) = log(LoggerCategory.System, message)
fun Logger.trace(message: String) = log(LoggerCategory.Trace, message)
fun Logger.info(message: String) = log(LoggerCategory.Info, message)
fun Logger.warn(message: String) = log(LoggerCategory.Warn, message)
fun Logger.error(message: String) = log(LoggerCategory.Error, message)