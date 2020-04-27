package cn.entertech.uicomponentsdk.utils

import kotlin.math.*

val NICE_TICK_COUNT = 5

/**
 * 友好坐标计算方式
 */
fun calNiceInterval(min: Double, max: Double): Int {
    var rawInterval = (max - min) / (NICE_TICK_COUNT - 1)
    val intervalMagnitude = ceil(10.0.pow(log10(rawInterval).toInt().toDouble())).toInt()
    val intervalSigDigit = (rawInterval / intervalMagnitude)
    val largeInterval = intervalSigDigit.niceCeil() * intervalMagnitude
    val smallInterval = intervalSigDigit.niceFloor() * intervalMagnitude
    val largeCount = getNiceCount(min, max, largeInterval)
    val smallCount = getNiceCount(min, max, smallInterval)
    return if (abs(largeCount - NICE_TICK_COUNT) < abs(smallCount - NICE_TICK_COUNT)) {
        largeInterval
    } else {
        smallInterval
    }
}

fun getNiceCount(min: Double, max: Double, interval: Int): Int {
    var first = floor(min / interval).toInt() * interval
    var last = ceil(max / interval).toInt() * interval
    return (last - first) / interval + 1
}

fun Double.niceCeil(): Int {
    if (this >= 0.0 && this < 1.0) {
        return 1
    } else if (this in 1.0..2.0) {
        return 2
    } else if (this > 2.0 && this <= 5.0) {
        return 5
    } else if (this > 5 && this <= 10) {
        return 10
    } else {
        return this.toInt()
    }
}

fun Double.niceFloor(): Int {
    if (this >= 0.0 && this < 2.0) {
        return 1
    } else if (this >= 2.0 && this < 5.0) {
        return 2
    } else if (this >= 5 && this < 10) {
        return 5
    } else {
        return this.toInt()
    }
}
