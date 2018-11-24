package org.lanark.geometry

data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
    val origin get() = Point(x, y)
    val size get() = Size(width, height)

    val left get() = x
    val right get() = x + width
    val top get() = y
    val bottom get() = y + height

    constructor(origin: Point, size: Size) : this(origin.x, origin.y, size.width, size.height)

    override fun toString() = "R($x, $y, $width, $height)"
    operator fun contains(point: Point): Boolean {
        return point.x >= x && point.x < x + width && point.y >= y && point.y < y + height
    }

    fun area(): Int = width * height
    fun perimeter(): Int = 2 * (width + height)

    fun intersects(other: Rect): Boolean {
        return x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y
    }

    fun isEmpty(): Boolean {
        return width == 0 || height == 0
    }

    fun relativeTo(other: Point) = Rect(x + other.x, y + other.y, width, height)
    
    fun relativeTo(other: Rect): Rect {
        val w = minOf(width, other.width - x) 
        val h = minOf(height, other.height - y) 
        return Rect(x + other.x, y + other.y, w, h)
    }

    companion object {
        val Empty = Rect(0, 0, 0, 0)
    }
}