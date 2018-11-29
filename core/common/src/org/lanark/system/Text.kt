package org.lanark.system

fun Int.toZeroPadding(size: Int): String {
    val value = toString()
    return "0".repeat(size - value.length) + value
}
