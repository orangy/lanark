package org.lanark.system

import kotlinx.cinterop.*
import platform.posix.*

actual class Time(private val value: time_t) {
    actual companion object {
        actual fun now(): Time = memScoped {
            val time = alloc<time_tVar>()
            // TODO: use clock or gettimeofday for better resolution
            time(time.ptr)
            Time(time.value)
        }
    }

    override fun toString(): String = memScoped {
        val time = alloc<time_tVar>()
        time.value = value
        val tm = localtime(time.ptr)?.pointed ?: return@memScoped "<null>"
        val year = (tm.tm_year + 1900).toString()
        val month = (tm.tm_mon + 1).toZeroPadding(2)
        val day = tm.tm_mday.toZeroPadding(2)
        val hour = tm.tm_hour.toZeroPadding(2)
        val minute = tm.tm_min.toZeroPadding(2)
        val seconds = tm.tm_sec.toZeroPadding(2)
        "$year/$month/$day $hour:$minute:$seconds"
    }

    private fun Int.toZeroPadding(size: Int): String {
        val value = toString()
        return "0".repeat(size - value.length) + value
    }
}