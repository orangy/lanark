package org.lanark.math

import kotlin.math.*
import kotlin.random.*

const val PIf = 3.141592653589793f
const val PI2f = PIf * 2
const val PI2 = PI * 2

const val degreesToRadians = PIf / 180
const val radiansToDegrees = 180f / PIf

inline fun square(value: Float) = value * value

fun Float.toDegrees() = this * radiansToDegrees
fun Float.toRadians() = this * degreesToRadians

val Float.degrees get() = toRadians()

fun Random.nextFloat(start: Float, end: Float) = start + nextFloat() * (end - start)
