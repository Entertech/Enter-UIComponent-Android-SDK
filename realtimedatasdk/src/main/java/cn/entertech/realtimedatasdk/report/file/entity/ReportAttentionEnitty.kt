package cn.entertech.realtimedatasdk.report.file.entity

class ReportAttentionEnitty{
    var attentionAvg:Double? = null
    var attentionRec:List<Double>? = null
    override fun toString(): String {
        return "ReportAttentionEnitty(attentionAvg=$attentionAvg, attentionRec=$attentionRec)"
    }

}