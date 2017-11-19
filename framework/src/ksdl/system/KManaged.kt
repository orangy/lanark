package ksdl.system

interface KManaged {
    fun release()
}

inline fun <TManaged : KManaged, TResult> TManaged.use(block: (TManaged) -> TResult): TResult {
    return try {
        block(this)
    } finally {
        release()
    }
}