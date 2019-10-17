package cn.entertech.realtimedatasdk.report.file.entity

class ReportPressureEnitty{
    var pressureAvg:Double? = null
    var pressureRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportPressureEnitty(pressureAvg=$pressureAvg, pressureRec=$pressureRec)"
    }

}