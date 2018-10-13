package org.lanark.system

expect class Time {
    companion object {
        actual fun now(): Time
    }
}