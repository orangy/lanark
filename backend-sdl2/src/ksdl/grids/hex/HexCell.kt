package ksdl.grids.hex

import platform.posix.*

data class HexCell(val q: Int, val r: Int, val s: Int = -q - r) {
    init {
        require(q + r + s == 0) { "Invalid cube coordinates: $this" }
    }

    operator fun plus(other: HexCell) = HexCell(q + other.q, r + other.r, s + other.s)
    operator fun minus(other: HexCell) = HexCell(q - other.q, r - other.r, s - other.s)
    operator fun times(times: Int) = HexCell(q * times, r * times, s * times)

    fun distance(other: HexCell) = minus(other).distanceFromZero()
    fun neighbor(index: Int) = plus(directions[index])
    fun neighbors() = (0..5).map { plus(directions[it]) }

    private fun distanceFromZero() = (abs(q) + abs(r) + abs(s)) / 2

    override fun toString() = "Hex($q, $r, $s)"

    companion object {
        val directions = listOf(
                HexCell(1, 0, -1), HexCell(1, -1, 0), HexCell(0, -1, 1),
                HexCell(-1, 0, 1), HexCell(-1, 1, 0), HexCell(0, 1, -1)
        )
    }
}

