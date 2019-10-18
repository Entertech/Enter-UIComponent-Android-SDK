package cn.entertech.realtimedatasdk.report.file.entity

class ReportRelaxationEnitty{
    var relaxationAvg:Double? = null
    var relaxationRec:List<Double>? = null
    override fun toString(): String {
        return "ReportRelaxationEnitty(relaxationAvg=$relaxationAvg, relaxationRec=$relaxationRec)"
    }

}