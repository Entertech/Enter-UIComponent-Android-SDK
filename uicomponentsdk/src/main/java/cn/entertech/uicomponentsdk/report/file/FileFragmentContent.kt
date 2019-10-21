package cn.entertech.naptime.model

import cn.entertech.uicomponentsdk.report.file.BrainDataUnit

/**
 * Created by EnterTech on 2017/12/20.
 */
class FileFragmentContent<T: BrainDataUnit> {
    val content = mutableListOf<T>()

    @Synchronized
    fun append(value: T) {
        content.add(value)
    }

    @Synchronized
    fun appendAll(values: ArrayList<T>) {
        content.addAll(values)
    }

    @Synchronized
    fun toFileBytes(): MutableList<Byte> {
        val bytes = mutableListOf<Byte>()
        content.forEach {
            bytes.addAll(it.toFileBytes().asList())
        }

        return bytes
    }
}