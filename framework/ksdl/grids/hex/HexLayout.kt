package ksdl.grids.hex

import ksdl.system.*
import sdl2.*

fun HexLayout(orientation: Double, radius: Int, origin: KPoint): HexLayout {
    val cornerVectors = (0 until 6).map { index ->
        val angle = 2 * KMath.pi * (orientation + index) / 6
        val x = radius * cos(angle)
        val y = radius * sin(angle)
        KVector(round(x).toInt(), round(y).toInt())
    }
    return HexLayout(cornerVectors, origin)
}

data class HexLayout(val vectors: List<KVector>, val origin: KPoint) {
    private val qVector = vectors[0] - vectors[4]
    private val rVector = vectors[1] - vectors[5]

    private val qBackTransform = 1.0 / (qVector.y * rVector.x - qVector.x * rVector.y)
    private val rBackTransform = 1.0 / (rVector.y * qVector.x - rVector.x * qVector.y)

    init {
        logger.trace("HexLayout: $qVector / $rVector : $vectors")
    }

    fun cellCenter(cell: HexCell): KPoint {
        val x = qVector.x * cell.q + rVector.x * cell.r
        val y = qVector.y * cell.q + rVector.y * cell.r
        return KPoint(x + origin.x, y + origin.y)
    }

    fun cellAt(point: KPoint): HexCell {
        val x = point.x - origin.x
        val y = point.y - origin.y
        val q = (rVector.x * y - rVector.y * x) * qBackTransform
        val r = (qVector.x * y - qVector.y * x) * rBackTransform
        return roundToCell(q, r, -q - r)
    }

    private fun roundToCell(q: Double, r: Double, s: Double): HexCell {
        val qInt = round(q).toInt()
        val rInt = round(r).toInt()
        val sInt = round(s).toInt()
        val qDiff = KMath.abs(qInt - q)
        val rDiff = KMath.abs(rInt - r)
        val sDiff = KMath.abs(sInt - s)
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