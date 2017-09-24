package ksdl

// TODO: May be use SDL_Log: https://wiki.libsdl.org/SDL_Log

interface KLogger {
    fun log(category: Category, message: String) = log(category) { message }
    fun log(category: Category, message: () -> String)

    fun switch(category: Category, enable: Boolean)

    enum class Category {
        Trace, Info, Warn, Error
    }
}

class KLoggerConsole : KLogger {
    private val disabledCategorySet = mutableSetOf<KLogger.Category>()

    override fun switch(category: KLogger.Category, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun log(category: KLogger.Category, message: () -> String) {
        if (disabledCategorySet.contains(category)) return
        println("${KTime.now()} ${category.name}: ${message()}")
    }
}

object KLoggerNone : KLogger {
    override fun log(category: KLogger.Category, message: () -> String) {}
    override fun switch(category: KLogger.Category, enable: Boolean) {}
}

fun KLogger.trace(message: String) = log(KLogger.Category.Trace, message)
fun KLogger.info(message: String) = log(KLogger.Category.Info, message)
fun KLogger.warn(message: String) = log(KLogger.Category.Warn, message)
fun KLogger.error(message: String) = log(KLogger.Category.Error, message)