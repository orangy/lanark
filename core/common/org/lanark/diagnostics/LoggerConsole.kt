package org.lanark.diagnostics

import org.lanark.application.*
import org.lanark.system.*

class LoggerConsole : Logger {
    private val disabledCategorySet = mutableSetOf<LoggerCategory>()
    private val colorMap = mutableMapOf<LoggerCategory, String>()

    fun color(category: LoggerCategory, color: String) {
        colorMap[category] = color
    }

    override fun switch(category: LoggerCategory, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun isEnabled(category: LoggerCategory): Boolean = !disabledCategorySet.contains(category)

    override fun log(category: LoggerCategory, message: String) {
        if (disabledCategorySet.contains(category)) return
        val color = categoryColor(category)
        println("${Time.now()} $color${category.name}: $message$resetColor")
    }

    private fun categoryColor(category: LoggerCategory): String {
        return colorMap[category] ?: ""
    }

    private companion object {
        const val resetColor = "\u001B[0m"
    }
}

inline fun EngineConfiguration.consoleLogger(configure: LoggerConsole.() -> Unit) {
    logger = LoggerConsole().apply(configure)
}
