package ksdl.system

// TODO: May be use SDL_Log: https://wiki.libsdl.org/SDL_Log

interface KLogger {
    fun log(category: Category, message: String) = log(category) { message }
    fun log(category: Category, message: () -> String)

    fun switch(category: Category, enable: Boolean)

    enum class Category {
        System, Trace, Info, Warn, Error
    }
}

class KLoggerConsole(private val colored: Boolean = true) : KLogger {
    private val disabledCategorySet = mutableSetOf<KLogger.Category>()

    override fun switch(category: KLogger.Category, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun log(category: KLogger.Category, message: () -> String) {
        if (disabledCategorySet.contains(category)) return
        val color = if (colored) when (category) {
            KLogger.Category.System -> gray
            KLogger.Category.Trace -> ""
            KLogger.Category.Info -> cyan
            KLogger.Category.Warn -> yellow
            KLogger.Category.Error -> red
        } else ""
        println("${KTime.now()} $color${category.name}: ${message()}$resetColor")
    }

    private companion object {
        val gray = "\u001B[0;37m";
        val cyan = "\u001B[0;36m";
        val yellow = "\u001B[0;33m";
        val red = "\u001B[0;31m";
        val resetColor = "\u001B[0m";
    }
}

object KLoggerNone : KLogger {
    override fun log(category: KLogger.Category, message: () -> String) {}
    override fun switch(category: KLogger.Category, enable: Boolean) {}
}

fun KLogger.system(message: String) = log(KLogger.Category.System, message)
fun KLogger.trace(message: String) = log(KLogger.Category.Trace, message)
fun KLogger.info(message: String) = log(KLogger.Category.Info, message)
fun KLogger.warn(message: String) = log(KLogger.Category.Warn, message)
fun KLogger.error(message: String) = log(KLogger.Category.Error, message)