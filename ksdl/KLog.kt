package ksdl

// TODO: May be use SDL_Log: https://wiki.libsdl.org/SDL_Log

interface KLog {
    fun log(category: Category, message: String) = log(category) { message }
    fun log(category: Category, message: () -> String)

    fun switch(category: Category, enable: Boolean)

    enum class Category {
        Trace, Info, Warn, Error
    }
}

class KLogConsole : KLog {
    private val disabledCategorySet = mutableSetOf<KLog.Category>()

    override fun switch(category: KLog.Category, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun log(category: KLog.Category, message: () -> String) {
        if (disabledCategorySet.contains(category)) return
        println("${KTime.now()} ${category.name}: ${message()}")
    }
}

object KLogNone : KLog {
    override fun log(category: KLog.Category, message: () -> String) {}
    override fun switch(category: KLog.Category, enable: Boolean) {}
}
