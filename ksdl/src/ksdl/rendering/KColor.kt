package ksdl.rendering

data class KColor(val red: UByte, val green: UByte, val blue: UByte, val alpha: UByte = 255u) {
    companion object {
        val WHITE = KColor(255u, 255u, 255u)
        val YELLOW = KColor(250u, 255u, 0u)
        val LIGHT_BROWN = KColor(210u, 150u, 0u)
        val ORANGE_BROWN = KColor(250u, 150u, 0u)
        val BROWN = KColor(240u, 130u, 0u)
        val DARK_BROWN = KColor(100u, 60u, 0u)
        val LIGHT_GRAY = KColor(150u, 150u, 150u)
        val GRAY = KColor(100u, 100u, 100u)
        val ALMOST_GRAY = KColor(102u, 102u, 102u)
        val DARK_GRAY = KColor(50u, 50u, 50u)
        val ALMOST_BLACK = KColor(20u, 20u, 20u)
        val ALMOST_DARK_GRAY = KColor(60u, 60u, 60u)
        val BLACK = KColor(0u, 0u, 0u)
        val ALMOST_WHITE = KColor(200u, 200u, 200u)
        val GREEN = KColor(0u, 255u, 0u)
        val LIGHT_GREEN = KColor(100u, 255u, 100u)
        val DARK_GREEN = KColor(0u, 150u, 0u)
        val RED = KColor(255u, 0u, 0u)
        val LIGHT_RED = KColor(255u, 100u, 100u)
        val PINK = KColor(255u, 20u, 147u)
        val ORANGE = KColor(255u, 165u, 0u)
        val BLUE = KColor(0u, 0u, 255u)
        val NIGHT_BLUE = KColor(0u, 0u, 20u)
        val DARK_BLUE = KColor(50u, 50u, 200u)
        val LIGHT_BLUE = KColor(100u, 100u, 255u)
        val PURPLE = KColor(160u, 32u, 240u)
        val VIOLET = KColor(120u, 0u, 255u)
        val TRANSLUCENT_BLACK = KColor(0u, 0u, 0u)
        val TRANSPARENT = KColor(0u, 0u, 0u, 0u)
    }
}

