package org.lanark.system

import kotlin.js.*

actual class Time(val date: Date) {
    actual companion object {
        actual fun now(): Time {
            return Time(Date())
        }
    }

    override fun toString(): String = date.toISOString()
}