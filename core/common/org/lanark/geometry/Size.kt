package org.lanark.geometry

data class Size(val width: Int, val height: Int) {
    operator fun plus(other: Size) = Size(width + other.width, height + other.height)
    operator fun minus(other: Size) = Size(width - other.width, height - other.height)
    operator fun div(other: Size) = Scale(width.toDouble() / other.width, height.toDouble() / other.height)
    operator fun times(scale: Scale) = Size((width * scale.horizontal).toInt(), (height * scale.vertical).toInt())
    override fun toString() = "{$width x $height}"
    operator fun times(scale: Double) = Size((width * scale).toInt(), (height * scale).toInt())

    companion object {
        val Empty = Size(0, 0)
    }
}

data class Scale(val horizontal: Double, val vertical: Double) {
    
    fun max() = maxOf(horizontal, vertical)
    fun min() = minOf(horizontal, vertical)
    
    companion object {
        val Identity = Scale(1.0, 1.0)
    }

}