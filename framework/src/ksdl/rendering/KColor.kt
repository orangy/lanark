package ksdl.rendering

data class KColor(val red: Int, val green: Int, val blue: Int, val alpha: Int = 255) {
    companion object {
        val WHITE = KColor(255, 255, 255)
        val YELLOW = KColor(250, 255, 0)
        val LIGHT_BROWN = KColor(210, 150, 0)
        val ORANGE_BROWN = KColor(250, 150, 0)
        val BROWN = KColor(240, 130, 0)
        val DARK_BROWN = KColor(100, 60, 0)
        val LIGHT_GRAY = KColor(150, 150, 150)
        val GRAY = KColor(100, 100, 100)
        val ALMOST_GRAY = KColor(102, 102, 102)
        val DARK_GRAY = KColor(50, 50, 50)
        val ALMOST_BLACK = KColor(20, 20, 20)
        val ALMOST_DARK_GRAY = KColor(60, 60, 60)
        val BLACK = KColor(0, 0, 0)
        val ALMOST_WHITE = KColor(200, 200, 200)
        val GREEN = KColor(0, 255, 0)
        val LIGHT_GREEN = KColor(100, 255, 100)
        val DARK_GREEN = KColor(0, 150, 0)
        val RED = KColor(255, 0, 0)
        val LIGHT_RED = KColor(255, 100, 100)
        val PINK = KColor(255, 20, 147)
        val ORANGE = KColor(255, 165, 0)
        val BLUE = KColor(0, 0, 255)
        val NIGHT_BLUE = KColor(0, 0, 20)
        val DARK_BLUE = KColor(50, 50, 200)
        val LIGHT_BLUE = KColor(100, 100, 255)
        val PURPLE = KColor(160, 32, 240)
        val VIOLET = KColor(120, 0, 255)
        val TRANSLUCENT_BLACK = KColor(0, 0, 0)
        val TRANSPARENT = KColor(0, 0, 0, 0)
    }
}

