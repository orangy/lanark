package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KTime(val value: time_t) {
    companion object {
        fun now(): KTime = memScoped {
            val time = alloc<time_tVar>()
            // TODO: use clock or gettimeofday for better resolution
            time(time.ptr)
            KTime(time.value)
        }
    }

    override fun toString(): String = memScoped {
        val time = alloc<time_tVar>()
        time.value = value
        val tm = localtime(time.ptr) ?: return@memScoped "<null>"
        val year = (tm.pointed.tm_year + 1900).toString()
        val month = (tm.pointed.tm_mon + 1).toZeroPadding(2)
        val day = tm.pointed.tm_mday.toZeroPadding(2)
        val hour = tm.pointed.tm_hour.toZeroPadding(2)
        val minute = tm.pointed.tm_min.toZeroPadding(2)
        val seconds = tm.pointed.tm_sec.toZeroPadding(2)
        "$year.$month.$day $hour:$minute:$seconds"
    }

    private fun Int.toZeroPadding(size: Int): String {
        val value = toString()
        return "0".repeat(size - value.length) + value
    }
}