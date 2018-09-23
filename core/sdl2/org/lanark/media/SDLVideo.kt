package org.lanark.media

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.system.*

actual class Video(val engine: Engine) : Managed {
    override fun release() {
        engine.logger.system("Released $this")
    }
}