package cn.entertech.uicomponentsdk.utils

import java.lang.Math.ceil
import java.util.ArrayList
import kotlin.math.ceil


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

fun curveByQuality(quality:ArrayList<Double>):ArrayList<Double>{
    val windowLen = ceil(quality.size / 40f).toInt()
    var index = 0
    val qualityFlags = DoubleArray(quality.size){0.0}
    while (index < quality.size-windowLen){
        val qualityRecSplit = quality.subList(index,index+windowLen)
        if (qualityRecSplit.filter { it>1 }.size >= 0.7 * windowLen){
            for (i in index until index + windowLen){
                qualityFlags[i] = 1.0
            }
        }else{
            for (i in index until index + windowLen){
                qualityFlags[i] = 0.0
            }
        }
        index += windowLen
    }
    return qualityFlags.toMutableList() as ArrayList<Double>
}
fun sampleData(data: List<Double>?, sample: Int): ArrayList<Double> {
    var sampleData = ArrayList<Double>()
    for (i in data!!.indices) {
        if (i % sample == 0) {
            sampleData.add(data[i])
        }
    }
    return sampleData
}