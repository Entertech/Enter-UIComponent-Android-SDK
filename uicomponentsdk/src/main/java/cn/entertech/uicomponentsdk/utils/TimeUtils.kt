package cn.entertech.uicomponentsdk.utils

import java.text.SimpleDateFormat

fun getChartAbsoluteTime(time: Long): String {
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return simpleDateFormat.format(time * 1000).split(" ")[1]
}

fun String.formatTime(beforeFormat: String, afterFormat: String): String {
    val timestamp = TimeUtils.getStringToDate(this,beforeFormat)
    return TimeUtils.getFormatTime(timestamp, afterFormat)
}