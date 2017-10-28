package ksdl.grids.hex

import ksdl.geometry.*

class HexMap<T : Any>(val size: KSize) : Iterable<Map.Entry<HexCell, T>> {
    private val cells = HashMap<HexCell, T>()

    override operator fun iterator(): Iterator<Map.Entry<HexCell, T>> = cells.iterator()

    companion object

    fun put(cell: HexCell, value: T) {
        cells.put(cell, value)
    }

    fun neighbors(cell: HexCell): List<Pair<HexCell, T>> {
        if (!cells.containsKey(cell))
            return emptyList()

        return cell.neighbors().mapNotNull { neighbor ->
            val value = cells[neighbor]
            value?.let { neighbor to it }
        }
    }
}

fun <T : Any> HexMap.Companion.buildCircle(radius: Int, value: T): HexMap<T> {
    val map = HexMap<T>(KSize(radius * 2 + 1, radius * 2 + 1))
    for (q in -radius..radius) {
        val r1 = maxOf(-radius, -q - radius)
        val r2 = minOf(radius, -q + radius)
        for (r in r1..r2) {
            map.put(HexCell(q, r), value)
        }
    }
    return map
}

fun <T : Any> HexMap.Companion.buildRect(width: Int, height: Int, value: T): HexMap<T> {
    val map = HexMap<T>(KSize(width, height))
    for (r in 0 until height) {
        val r_offset = r / 2
        for (q in -r_offset until width - r_offset) {
            map.put(HexCell(q, r), value)
        }
    }
    return map
}

