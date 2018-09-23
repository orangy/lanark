package org.lanark.drawing

data class Color(val red: UByte, val green: UByte, val blue: UByte, val alpha: UByte = 255u) {
    companion object {
        val WHITE = Color(255u, 255u, 255u)
        val YELLOW = Color(250u, 255u, 0u)
        val LIGHT_BROWN = Color(210u, 150u, 0u)
        val ORANGE_BROWN = Color(250u, 150u, 0u)
        val BROWN = Color(240u, 130u, 0u)
        val DARK_BROWN = Color(100u, 60u, 0u)
        val LIGHT_GRAY = Color(150u, 150u, 150u)
        val GRAY = Color(100u, 100u, 100u)
        val ALMOST_GRAY = Color(102u, 102u, 102u)
        val DARK_GRAY = Color(50u, 50u, 50u)
        val ALMOST_BLACK = Color(20u, 20u, 20u)
        val ALMOST_DARK_GRAY = Color(60u, 60u, 60u)
        val BLACK = Color(0u, 0u, 0u)
        val ALMOST_WHITE = Color(200u, 200u, 200u)
        val GREEN = Color(0u, 255u, 0u)
        val LIGHT_GREEN = Color(100u, 255u, 100u)
        val DARK_GREEN = Color(0u, 150u, 0u)
        val RED = Color(255u, 0u, 0u)
        val LIGHT_RED = Color(255u, 100u, 100u)
        val PINK = Color(255u, 20u, 147u)
        val ORANGE = Color(255u, 165u, 0u)
        val BLUE = Color(0u, 0u, 255u)
        val NIGHT_BLUE = Color(0u, 0u, 20u)
        val DARK_BLUE = Color(50u, 50u, 200u)
        val LIGHT_BLUE = Color(100u, 100u, 255u)
        val PURPLE = Color(160u, 32u, 240u)
        val VIOLET = Color(120u, 0u, 255u)
        val TRANSLUCENT_BLACK = Color(0u, 0u, 0u)
        val TRANSPARENT = Color(0u, 0u, 0u, 0u)
    }
}

