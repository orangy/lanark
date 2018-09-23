package org.lanark.geometry

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Point(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Point(x - other.x, y - other.y)
    override fun toString() = "[$x, $y]"

    fun relativeTo(other: Point) = Point(x + other.x, y + other.y)

    companion object {
        val Zero = Point(0, 0)
    }
}