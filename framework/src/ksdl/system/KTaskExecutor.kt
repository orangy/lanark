package ksdl.system

interface KTaskExecutor {
    fun submit(task: () -> Unit)

    fun run()
    val before: KSignal<Unit>
    val after: KSignal<Unit>
    fun stop()
}

