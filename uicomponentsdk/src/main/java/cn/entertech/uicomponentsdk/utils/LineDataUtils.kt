package cn.entertech.uicomponentsdk.utils

import java.util.ArrayList


fun formatData(datas: List<Double>): List<Double>{
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
    return newData
}