package cn.entertech.realtimedatasdk.report.file.entity

class ReportEEGDataEntity{
    var alphaCurve:List<Double>? = null
    var betaCurve:List<Double>? = null
    var thetaCurve:List<Double>? = null
    var deltaCurve:List<Double>? = null
    var gammaCurve:List<Double>? = null

    override fun toString(): String {
        return "ReportEEGDataEntity(alphaCurve=$alphaCurve, betaCurve=$betaCurve, thetaCurve=$thetaCurve, deltaCurve=$deltaCurve, gammaCurve=$gammaCurve)"
    }


}