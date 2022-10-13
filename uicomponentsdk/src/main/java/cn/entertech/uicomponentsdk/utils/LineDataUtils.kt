package cn.entertech.uicomponentsdk.utils

import java.util.ArrayList


fun formatData(datas: List<Double>): MutableList<Double> {
    var newData = ArrayList<Double>()
    var firstNotZeroValue = 0.0
    for (data in datas) {
        if (data != 0.0) {
            firstNotZeroValue = data
            break
        }
    }
    var lastValue = 0.0
    for (i in datas.indices) {
        if (datas[i] == 0.0) {
            if (i == 0) {
                newData.add(firstNotZeroValue)
                lastValue = firstNotZeroValue
            } else {
                newData.add(lastValue)
            }
        } else {
            newData.add(datas[i])
            lastValue = datas[i]
        }
    }
    return smoothData(newData)
}

fun smoothData(datas: MutableList<Double>): MutableList<Double> {
    var newData = ArrayList<Double>()
    if (datas.isEmpty()) {
        return newData
    } else {
        for (i in datas.indices) {
            if (i == 0 || i == datas.size - 1) {
                newData.add(datas[i])
            } else {
                val average = (datas[i - 1] + datas[i] + datas[i+1]) / 3
                newData.add(average)
            }
        }
        return newData
    }
}