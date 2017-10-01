package ksdl.system

// TODO: May be use SDL_Log: https://wiki.libsdl.org/SDL_Log

interface KLogger {
    fun log(category: KLogCategory, message: String) = log(category) { message }
    fun log(category: KLogCategory, message: () -> String)

    fun switch(category: KLogCategory, enable: Boolean)
}

class KLogCategory(val name: String, val color: String = "") {
    companion object {
        val System = KLogCategory("System", "\u001B[0;37m")
        val Trace = KLogCategory("Trace")
        val Info = KLogCategory("Info", "\u001B[0;34m")
        val Warn = KLogCategory("Warn", "\u001B[0;33m")
        val Error = KLogCategory("Error", "\u001B[0;31m")
    }
}

class KLoggerConsole(private val colored: Boolean = true) : KLogger {
    private val disabledCategorySet = mutableSetOf<KLogCategory>()

    override fun switch(category: KLogCategory, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun log(category: KLogCategory, message: () -> String) {
        if (disabledCategorySet.contains(category)) return
        val color = if (colored) category.color else ""
        println("${KTime.now()} $color${category.name}: ${message()}$resetColor")
    }

    private companion object {
        val resetColor = "\u001B[0m"
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