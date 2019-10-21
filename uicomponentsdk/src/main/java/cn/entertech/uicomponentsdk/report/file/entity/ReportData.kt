package cn.entertech.uicomponentsdk.report.file.entity

class ReportData {
    var startTime:Long? =  null
    var  reportPleasureEnitty: ReportPleasureEnitty? = null
    var reportAttentionEnitty: ReportAttentionEnitty? = null
    var reportPressureEnitty: ReportPressureEnitty? = null
    var reportRelaxationEnitty: ReportRelaxationEnitty? = null
    var reportHRDataEntity: ReportHRDataEntity? = null
    var reportEEGDataEntity: ReportEEGDataEntity? = null
    override fun toString(): String {
        return "ReportData(startTime=$startTime, reportPleasureEnitty=$reportPleasureEnitty, reportAttentionEnitty=$reportAttentionEnitty, reportPressureEnitty=$reportPressureEnitty, reportRelaxationEnitty=$reportRelaxationEnitty, reportHRDataEntity=$reportHRDataEntity, reportEEGDataEntity=$reportEEGDataEntity)"
    }

}