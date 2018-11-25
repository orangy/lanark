package org.lanark.geometry

data class Size(val width: Int, val height: Int) {
    operator fun plus(other: Size) = Size(width + other.width, height + other.height)
    operator fun minus(other: Size) = Size(width - other.width, height - other.height)
    operator fun div(other: Size) = Scale(width.toDouble() / other.width, height.toDouble() / other.height)
    operator fun div(scale: Double) = Size((width / scale).toInt(), (height / scale).toInt())
    operator fun times(scale: Scale) = Size((width * scale.horizontal).toInt(), (height * scale.vertical).toInt())
    override fun toString() = "{$width x $height}"
    operator fun times(scale: Double) = Size((width * scale).toInt(), (height * scale).toInt())

    fun toVector() = Vector(width, height)
    
    companion object {
        val Empty = Size(0, 0)
    }
}

