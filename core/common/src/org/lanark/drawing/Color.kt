package org.lanark.drawing

data class Color(val red: UByte, val green: UByte, val blue: UByte, val alpha: UByte = 255u) {
    companion object {
        val White = Color(255u, 255u, 255u)
        val Yellow = Color(250u, 255u, 0u)
        val LightBrown = Color(210u, 150u, 0u)
        val OrangeBrown = Color(250u, 150u, 0u)
        val Brown = Color(240u, 130u, 0u)
        val DarkBrown = Color(100u, 60u, 0u)
        val LightGray = Color(150u, 150u, 150u)
        val Gray = Color(100u, 100u, 100u)
        val AlmostGray = Color(102u, 102u, 102u)
        val DarkGray = Color(50u, 50u, 50u)
        val AlmostBlack = Color(20u, 20u, 20u)
        val AlmostDarkGray = Color(60u, 60u, 60u)
        val Black = Color(0u, 0u, 0u)
        val AlmostWhite = Color(200u, 200u, 200u)
        val Green = Color(0u, 255u, 0u)
        val LightGreen = Color(100u, 255u, 100u)
        val DarkGreen = Color(0u, 150u, 0u)
        val Red = Color(255u, 0u, 0u)
        val LightRed = Color(255u, 100u, 100u)
        val Pink = Color(255u, 20u, 147u)
        val Orange = Color(255u, 165u, 0u)
        val Blue = Color(0u, 0u, 255u)
        val NightBlue = Color(0u, 0u, 20u)
        val DarkBlue = Color(50u, 50u, 200u)
        val LightBlue = Color(100u, 100u, 255u)
        val Purple = Color(160u, 32u, 240u)
        val Violet = Color(120u, 0u, 255u)
        val TranslucentBlack = Color(0u, 0u, 0u)
        val Transparent = Color(0u, 0u, 0u, 0u)
    }
}

