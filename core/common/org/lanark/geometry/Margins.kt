package org.lanark.geometry

data class Margins(val top: Int, val left: Int, val bottom: Int, val right: Int) {
    companion object {
        val Empty = Margins(0, 0, 0, 0)
    }
}