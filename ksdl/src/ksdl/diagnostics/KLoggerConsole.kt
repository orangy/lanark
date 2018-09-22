package ksdl.diagnostics

import ksdl.system.*

class KLoggerConsole : KLogger {
    private val disabledCategorySet = mutableSetOf<KLogCategory>()
    private val colorMap = mutableMapOf<KLogCategory, String>()

    fun color(category: KLogCategory, color: String) {
        colorMap[category] = color
    }

    override fun switch(category: KLogCategory, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun isEnabled(category: KLogCategory): Boolean = !disabledCategorySet.contains(category)

    override fun log(category: KLogCategory, message: String) {
        if (disabledCategorySet.contains(category)) return
        val color = categoryColor(category)
        println("${KTime.now()} $color${category.name}: $message$resetColor")
    }

    private fun categoryColor(category: KLogCategory): String {
        return colorMap[category] ?: ""
    }

    private companion object {
        const val resetColor = "\u001B[0m"
    }
}

inline fun KPlatform.Configuration.consoleLogger(configure: KLoggerConsole.() -> Unit) {
    logger = KLoggerConsole().apply(configure)
}