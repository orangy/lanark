package org.lanark.playground.hex

import kotlin.random.*

class HexCellDescriptor(val type: HexLandType) {

}

enum class HexLandType {
    Water, Land
}


fun HexMap<HexCellDescriptor>.buildLand(seeds: List<HexCell>): HexMap<HexCellDescriptor> {
    val land = mapNotNull { entry ->
        val currentCell = entry.key
        val neighbors = neighbors(currentCell)
        if (neighbors.size != 6)
            return@mapNotNull null // edge cell, leave it alone as water
        val nearestSeed = seeds.minBy { it.distance(currentCell) } ?: return@mapNotNull null
        val distance = nearestSeed.distance(currentCell)
        if (distance == 0 || Random.nextInt() % distance < 10)
            currentCell to HexLandType.Land
        else
            null

    }
    land.forEach {
        put(it.first, HexCellDescriptor(it.second))
    }
    return this
}