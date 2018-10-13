package org.lanark.application

import org.lanark.diagnostics.*
import platform.Foundation.*

class NSLogger : Logger {
    private val disabledCategorySet = mutableSetOf<LoggerCategory>()
    
    override fun switch(category: LoggerCategory, enable: Boolean) {
        if (enable) disabledCategorySet.remove(category) else disabledCategorySet.add(category)
    }

    override fun isEnabled(category: LoggerCategory): Boolean = !disabledCategorySet.contains(category)

    override fun log(category: LoggerCategory, message: String) {
        if (disabledCategorySet.contains(category)) return
        NSLog("${category.name}: $message")
    }
}
