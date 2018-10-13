package org.lanark.media

import org.lanark.system.*

expect class Music : Managed {
    fun play(repeat: Int? = null)
    fun stop()
}