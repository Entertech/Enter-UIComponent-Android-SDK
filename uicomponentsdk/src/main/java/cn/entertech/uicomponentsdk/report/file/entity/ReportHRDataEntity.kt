package cn.entertech.uicomponentsdk.report.file.entity

class ReportHRDataEntity{
    var hrAvg:Double? = null
    var hrMax:Double? = null
    var hrMin:Double? = null
    var hrvAvg:Double? = null
    var hrRec:List<Double>? = null
    var hrvRec:List<Double>? = null
    override fun toString(): String {
        return "ReportHRDataEntity(hrAvg=$hrAvg, hrMax=$hrMax, hrMix=$hrMin, hrvAvg=$hrvAvg, hrRec=$hrRec, hrvRec=$hrvRec)"
    }

}