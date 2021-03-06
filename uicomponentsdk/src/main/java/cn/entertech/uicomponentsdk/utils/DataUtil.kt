package cn.entertech.uicomponentsdk.utils

fun removeZeroData(source: List<Double>): ArrayList<Double> {
    var source1 = source as ArrayList<Double>
    var values = ArrayList<Double>()
    for (i in 0 until source1.size) {
        if (source1[i] != 0.0) {
            values.add(source1[i])
        }
    }
    return values
}