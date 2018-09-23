package org.lanark.application

import org.lanark.system.*

interface TaskExecutor {
    fun submit(task: () -> Unit)

    fun run()
    val before: Signal<Unit>
    val after: Signal<Unit>
    fun stop()
}

