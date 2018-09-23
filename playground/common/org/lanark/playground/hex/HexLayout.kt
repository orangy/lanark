package org.lanark.playground.hex

import org.lanark.geometry.*
import org.lanark.system.*

fun HexLayout(orientation: Double, radius: Int): HexLayout {
    val cornerVectors = (0 until 6).map { index ->
        val angle = 2 * Math.pi * (orientation + index) / 6
        val x = radius * Math.cos(angle)
        val y = radius * Math.sin(angle)
        Vector(Math.round(x).toInt(), Math.round(y).toInt())
    }
    return HexLayout(cornerVectors)
}

data class HexLayout(private val vectors: List<Vector>) {
    private val qVector = vectors[0] - vectors[4]
    private val rVector = vectors[1] - vectors[5]

    private val qBackTransform = 1.0 / (qVector.y * rVector.x - qVector.x * rVector.y)
    private val rBackTransform = 1.0 / (rVector.y * qVector.x - rVector.x * qVector.y)

    private val polygonIndices = listOf(1, 2, 3, 4, 5, 0)

    init {
        //logger.trace("HexLayout: $qVector / $rVector : $vectors")
    }

    fun cellCenter(cell: HexCell): Point {
        val x = qVector.x * cell.q + rVector.x * cell.r
        val y = qVector.y * cell.q + rVector.y * cell.r
        return Point(x, y)
    }

    fun cellAt(point: Point): HexCell {
        val x = point.x
        val y = point.y
        val q = (rVector.x * y - rVector.y * x) * qBackTransform
        val r = (qVector.x * y - qVector.y * x) * rBackTransform
        return roundToCell(q, r, -q - r)
    }

    fun polygon(hex: HexCell): List<Point> {
        val center = cellCenter(hex)
        return vectors.map { center + it }
    }


    private fun roundToCell(q: Double, r: Double, s: Double): HexCell {
        val qInt = Math.round(q).toInt()
        val rInt = Math.round(r).toInt()
        val sInt = Math.round(s).toInt()
        val qDiff = Math.abs(qInt - q)
        val rDiff = Math.abs(rInt - r)
        val sDiff = Math.abs(sInt - s)
        return when {
            qDiff > rDiff && qDiff > sDiff -> HexCell(-rInt - sInt, rInt, sInt)
            rDiff > sDiff -> HexCell(qInt, -qInt - sInt, sInt)
            else -> HexCell(qInt, rInt, -qInt - rInt)
        }
    }

    companion object {
        val orientationVertical = -0.5
        val orientationHorizontal = 0.0
    }
}