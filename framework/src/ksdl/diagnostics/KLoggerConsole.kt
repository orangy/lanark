package ksdl.diagnostics

import ksdl.system.*

class KLoggerConsole : KLogger {
    private val disabledCategorySet = mutableSetOf<KLogCategory>()
    private val colorMap = mutableMapOf<KLogCategory, String>()

    fun color(category: KLogCategory, color: String) {
        colorMap.put(category, color)
    }

    override fun switch(category: KLogCategory, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun log(category: KLogCategory, message: () -> String) {
        if (disabledCategorySet.contains(category)) return
        val color = categoryColor(category)
        println("${KTime.now()} $color${category.name}: ${message()}$resetColor")
    }

    private fun categoryColor(category: KLogCategory): String {
        return colorMap[category] ?: ""
    }

    private companion object {
        val resetColor = "\u001B[0m"
    }
}

inline fun consoleLogger() = KLoggerConsole()
inline fun consoleLogger(configure: KLoggerConsole.() -> Unit) = KLoggerConsole().apply(configure)