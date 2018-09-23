package org.lanark.system

expect object Math {
    val pi: Double
    fun cos(value: Double): Double
    fun sin(value: Double): Double
    fun abs(value: Double): Double
    fun abs(value: Int): Int
    
    fun pow(value: Int, power: Int): Int
    fun round(value: Double, decimals: Int): Double
    fun round(value: Double): Double
    fun floor(value: Double): Double
    fun sqrt(value: Double): Double
}