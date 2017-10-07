package ksdl.grids.hex

import ksdl.system.*
import sdl2.*

data class HexLayout(val orientation: Double, val size: KSize, val origin: KPoint) {
    val cornerVectors = (0..5).map { index ->
        val angle = KMath.pi * (orientation + index) / 3
        val x = size.width * cos(angle)
        val y = size.height * sin(angle)
        KVector(round(x).toInt(), round(y).toInt())
    }

    private val qVector = cornerVectors[0] - cornerVectors[4]
    private val rVector = cornerVectors[1] - cornerVectors[5]

    private val qBackTransform = 1.0 / (qVector.y * rVector.x - qVector.x * rVector.y)
    private val rBackTransform = 1.0 / (rVector.y * qVector.x - rVector.x * qVector.y)

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
        val orientationVertical = 0.5
        val orientationHorizontal = 0.0
    }
}