package org.lanark.system

import java.time.*

actual class Time(private val dateTime: LocalDateTime) {
    actual companion object {
        actual fun now(): Time = Time(LocalDateTime.now())
    }

    override fun toString() = dateTime.toString()
}