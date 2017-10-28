package ksdl.geometry

data class KSize(val width: Int, val height: Int) {
    operator fun plus(other: KSize) = KSize(width + other.width, height + other.height)
    operator fun minus(other: KSize) = KSize(width - other.width, height - other.height)
    override fun toString() = "{$width x $height}"
    operator fun times(scale: Double) = KSize((width * scale).toInt(), (height * scale).toInt())
}

