package org.lanark.geometry

data class Scale(val horizontal: Double, val vertical: Double) {

    fun max() = maxOf(horizontal, vertical)
    fun min() = minOf(horizontal, vertical)

    companion object {
        val Identity = Scale(1.0, 1.0)
    }

}