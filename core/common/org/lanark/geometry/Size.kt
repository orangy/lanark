package org.lanark.geometry

data class Size(val width: Int, val height: Int) {
    operator fun plus(other: Size) = Size(width + other.width, height + other.height)
    operator fun minus(other: Size) = Size(width - other.width, height - other.height)
    override fun toString() = "{$width x $height}"
    operator fun times(scale: Double) = Size((width * scale).toInt(), (height * scale).toInt())

    companion object {
        val Empty = Size(0, 0)
    }
}

