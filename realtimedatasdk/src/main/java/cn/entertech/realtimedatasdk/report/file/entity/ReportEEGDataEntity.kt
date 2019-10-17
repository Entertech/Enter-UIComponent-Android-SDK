package cn.entertech.realtimedatasdk.report.file.entity

class ReportEEGDataEntity{
    var alphaCurve:ArrayList<Double>? = null
    var betaCurve:ArrayList<Double>? = null
    var thetaCurve:ArrayList<Double>? = null
    var deltaCurve:ArrayList<Double>? = null
    var gammaCurve:ArrayList<Double>? = null

    override fun toString(): String {
        return "ReportEEGDataEntity(alphaCurve=$alphaCurve, betaCurve=$betaCurve, thetaCurve=$thetaCurve, deltaCurve=$deltaCurve, gammaCurve=$gammaCurve)"
    }


}