package ksdl.geometry

data class KPoint(val x: Int, val y: Int) {
    operator fun plus(other: KVector) = KPoint(x + other.x, y + other.y)
    operator fun minus(other: KVector) = KPoint(x - other.x, y - other.y)
    override fun toString() = "[$x, $y]"

    fun relativeTo(other: KPoint) = KPoint(x + other.x, y + other.y)

    companion object {
        val Zero = KPoint(0, 0)
    }
}