package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.uicomponentsdk.R

class CommonReportCurveCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0, layoutId: Int? = null
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var titleMenuIconColor: Int = Color.parseColor("#6D6E7D")
    private var titleMenuIcon: Drawable? = null
    private var splitLineColor: Int = Color.parseColor("#D3D3D3")
    private var subTitleColor: Int = Color.parseColor("#000000")
    private var subTitle: String = ""
    private var titleColor: Int = Color.BLUE
    private var title: String = ""
    private var titleIcon: Drawable? = null
    private var mSelfView: View? = null

    init {
        mSelfView =
            LayoutInflater.from(context).inflate(R.layout.view_common_report_curve_card, null)
        val layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView!!.layoutParams = layoutParams
        addView(mSelfView)
        val typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.CommonReportCurveCard
        )
        titleIcon = typeArray.getDrawable(R.styleable.CommonReportCurveCard_crcc_titleIcon)
        titleMenuIcon = typeArray.getDrawable(R.styleable.CommonReportCurveCard_crcc_titleMenuIcon)
        titleMenuIconColor = typeArray.getColor(R.styleable.CommonReportCurveCard_crcc_titleMenuIconColor,titleMenuIconColor)
        title = typeArray.getString(R.styleable.CommonReportCurveCard_crcc_title) ?: ""
        titleColor =
            typeArray.getColor(R.styleable.CommonReportCurveCard_crcc_titleColor, Color.BLUE)
        subTitle = typeArray.getString(R.styleable.CommonReportCurveCard_crcc_subTitle) ?: ""
        subTitleColor =
            typeArray.getColor(R.styleable.CommonReportCurveCard_crcc_subTitleColor, subTitleColor)
        splitLineColor = typeArray.getColor(
            R.styleable.CommonReportCurveCard_crcc_splitLineColor,
            splitLineColor
        )
        typeArray.recycle()
        initView()
    }

    fun initView() {
        if (titleIcon != null) {
            mSelfView?.findViewById<ImageView>(R.id.iv_title_icon)?.setImageDrawable(titleIcon!!)
        }
        if (titleMenuIcon != null){
            mSelfView?.findViewById<ImageView>(R.id.iv_menu_icon)?.imageTintList = ColorStateList.valueOf(titleMenuIconColor)
            mSelfView?.findViewById<ImageView>(R.id.iv_menu_icon)?.setImageDrawable(titleMenuIcon!!)
        }
        mSelfView?.findViewById<TextView>(R.id.tv_title)?.text = title
        mSelfView?.findViewById<TextView>(R.id.tv_title)?.setTextColor(titleColor)
        mSelfView?.findViewById<TextView>(R.id.tv_sub_title)?.text = subTitle
        mSelfView?.findViewById<TextView>(R.id.tv_sub_title)?.setTextColor(subTitleColor)
    }

    fun setContentView(contentView: View) {
        contentView.layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView?.findViewById<RelativeLayout>(R.id.rl_content_view)?.addView(contentView)
    }

    fun setSubTitle(subTitle:String){
        this.subTitle = subTitle
        initView()
    }

}