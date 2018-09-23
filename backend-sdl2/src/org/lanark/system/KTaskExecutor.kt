package org.lanark.system

interface KTaskExecutor {
    fun submit(task: () -> Unit)

    fun run()
    val before: Signal<Unit>
    val after: Signal<Unit>
    fun stop()
}

