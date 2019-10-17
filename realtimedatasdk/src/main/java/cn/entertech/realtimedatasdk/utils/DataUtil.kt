package cn.entertech.realtimedatasdk.utils

fun removeZeroData(source: List<Double>): ArrayList<Double> {
    var source = source as ArrayList<Double>
    var values = ArrayList<Double>()
    for (i in 0 until source.size) {
        if (source[i] != 0.0) {
            values.add(source[i])
        }
    }
    return values
}