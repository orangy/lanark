package org.lanark.system

interface Managed {
    fun release()
}

inline fun <TManaged : Managed, TResult> TManaged.use(block: (TManaged) -> TResult): TResult {
    return try {
        block(this)
    } finally {
        release()
    }
}