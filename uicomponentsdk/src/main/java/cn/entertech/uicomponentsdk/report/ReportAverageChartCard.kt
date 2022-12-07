package cn.entertech.uicomponentsdk.report

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.PopupMenu
import cn.entertech.uicomponentsdk.R
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.layout_average_bar_card.view.*
import kotlinx.android.synthetic.main.layout_common_card_title.view.*
import java.lang.Math.ceil

class ReportAverageChartCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var onInfoClickListener: (() -> Unit)? = null
    private var mAverageLineColor: Int = Color.parseColor("#FB9C98")
    private var mLevels: List<Level>? = null
    private var mShowLevelOnly: Boolean = false
    private var mSelectorTextColor: Int = Color.parseColor("#080A0E")
    private var mSelectorIconColor: Int = Color.parseColor("#FFFFFF")
    private var mSelectorBgColor: Int = Color.parseColor("#F2F2F7")
    lateinit var items: List<String>
    private var valueList: List<List<Float>>? = null
    private var mValueEqualIcon: Drawable? = null
    private var mValueSmallerIcon: Drawable? = null
    private var mValueBiggerIcon: Drawable? = null
    private var mIsAverageInt: Boolean = false
    private var mBarHighLightColor: Int = Color.parseColor("#FFE4BB")
    private var mTag: String? = ""
    private var mUnit: String? = ""
    private var mInfoUrl: String? = ""
    private var mBarValueBgColor: Int = Color.parseColor("#FFE4BB")
    private var mBarColor: Int = Color.parseColor("#EAECF1")
    private var mIsShowTitleIcon: Boolean = true
    private var mIsShowTitleMenuIcon: Boolean = true
    private var mTitle: String? = null
    private var mSpectrumColors: List<Int>? = null
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_average_bar_card, null)

    private var mIsMenuIconInfo: Boolean = false
    private var mMainColor: Int = Color.parseColor("#0064ff")
    private var mTextColor: Int = Color.parseColor("#171726")
    private var mBg: Drawable? = null
    private var mTiltleIcon: Drawable?
    private var mTitleMenuIcon: Drawable?

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        var typeArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReportAverageChartCard
        )
        mMainColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_mainColor,
            mMainColor
        )
        mTextColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_textColor,
            mTextColor
        )
        mBg = typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_background)
        mTiltleIcon =
            typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_titleIcon)
        mTitleMenuIcon =
            typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_titleMenuIcon)

        mIsShowTitleMenuIcon =
            typeArray.getBoolean(
                R.styleable.ReportAverageChartCard_racc_isTitleMenuIconShow,
                true
            )
        mIsShowTitleIcon = typeArray.getBoolean(
            R.styleable.ReportAverageChartCard_racc_isTitleIconShow,
            true
        )
        mTitle =
            typeArray.getString(R.styleable.ReportAverageChartCard_racc_title)
        mTag =
            typeArray.getString(R.styleable.ReportAverageChartCard_racc_tag)
        mUnit = typeArray.getString(R.styleable.ReportAverageChartCard_racc_unit)
        mBarColor = typeArray.getColor(R.styleable.ReportAverageChartCard_racc_barColor, mBarColor)
        mAverageLineColor = typeArray.getColor(R.styleable.ReportAverageChartCard_racc_averageLineColor, mAverageLineColor)
        mBarHighLightColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_barHighLightColor,
            mBarHighLightColor
        )
        mBarValueBgColor =
            typeArray.getColor(R.styleable.ReportAverageChartCard_racc_barValueBgColor, mBarColor)
        mInfoUrl = typeArray.getString(R.styleable.ReportAverageChartCard_racc_infoUrl)
        mIsMenuIconInfo =
            typeArray.getBoolean(R.styleable.ReportAverageChartCard_racc_isMenuIconInfo, false)
        mValueBiggerIcon =
            typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_valueBiggerIcon)
        mValueSmallerIcon =
            typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_valueSmallerIcon)
        mValueEqualIcon =
            typeArray.getDrawable(R.styleable.ReportAverageChartCard_racc_valueEqualIcon)
        mSelectorBgColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_selectorBgColor,
            mSelectorBgColor
        )
        mSelectorTextColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_selectorTextColor,
            mSelectorTextColor
        )
        mSelectorIconColor = typeArray.getColor(
            R.styleable.ReportAverageChartCard_racc_selectorIconColor,
            mSelectorIconColor
        )
        mShowLevelOnly =
            typeArray.getBoolean(R.styleable.ReportAverageChartCard_racc_showLevelOnly, false)
        initView()

    }

    private fun showMenu(v: View, menuRes: Int) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                return true
            }

        })
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    var isWindowShow = false

    fun initPopupWindow() {
        val listPopupWindowButton = mSelfView.findViewById<MaterialButton>(R.id.btn_selector)
        listPopupWindowButton.setBackgroundColor(mSelectorBgColor)
        listPopupWindowButton.setTextColor(mSelectorTextColor)
//        listPopupWindowButton.iconTintMode = PorterDuff.Mode.SRC_ATOP
//        listPopupWindowButton.iconTint = ColorStateList.valueOf(mSelectorIconColor)
        listPopupWindowButton.icon.mutate()
        listPopupWindowButton.icon.colorFilter = PorterDuffColorFilter(mSelectorIconColor,PorterDuff.Mode.SRC_ATOP)
//        listPopupWindowButton.setIconTintResource(Color.parseColor("#ffffff"))
        listPopupWindowButton.visibility = View.VISIBLE
        listPopupWindowButton.text = items[0]
//        listPopupWindowButton.setOnClickListener {
//            showMenu(it,R.menu.brainwave_menu)
//        }
        val listPopupWindow = ListPopupWindow(context!!, null)

// Set button as the list popup's anchor
        listPopupWindow.anchorView = listPopupWindowButton

// Set list popup's content
        val adapter = ArrayAdapter(context, R.layout.item_brainwave_select, items)
        listPopupWindow.setAdapter(adapter)

// Set list popup's item click listener
        listPopupWindow.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            // Respond to list popup window item click.
            listPopupWindowButton.text = items[position]
            setValues(valueList!![position],items[position])
            // Dismiss popup.
            listPopupWindow.dismiss()
            isWindowShow = false
        }

// Show list popup window on button click.
        listPopupWindowButton.setOnClickListener { v: View? ->
            isWindowShow = if (!isWindowShow) {
                listPopupWindow.show()
                true
            } else {
                listPopupWindow.dismiss()
                false
            }
        }
    }

    fun initView() {
        tv_title.text = mTitle
        tv_title.setTextColor(mMainColor)
        if (mIsShowTitleIcon) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageDrawable(mTiltleIcon)
        } else {
            iv_icon.visibility = View.GONE
        }
        if (mIsShowTitleMenuIcon) {
            iv_menu.visibility = View.VISIBLE
            iv_menu.setImageDrawable(mTitleMenuIcon)
        } else {
            iv_menu.visibility = View.GONE
        }
        if (mIsMenuIconInfo && mInfoUrl != null) {
            iv_menu.setOnClickListener {
                onInfoClickListener?.invoke()
            }
        }
        initChart()
    }

    fun initChart() {
        average_bar_chart.setAverageInt(mIsAverageInt)
        average_bar_chart.setUnit(mUnit)
        average_bar_chart.setAverageLineColor(mAverageLineColor)
        average_bar_chart.setBarColor(mBarColor)
        average_bar_chart.setPrimaryTextColor(mMainColor)
        average_bar_chart.setSecondTextColor(mTextColor)
        average_bar_chart.setBarHighLightColor(mBarHighLightColor)
        average_bar_chart.setBarValueBgColor(mBarValueBgColor)
        average_bar_chart.setLevels(mLevels)
        average_bar_chart.setShowLevelOnly(mShowLevelOnly)

        if (mBg != null && mBg is ColorDrawable) {
            average_bar_chart.setBgColor((mBg as ColorDrawable).color)
            ll_bg.setBackgroundColor((mBg as ColorDrawable).color)
        }
    }

    fun setIsValueFloat(isValueFloat: Boolean) {
        average_bar_chart.setIsValueFloat(isValueFloat)
    }

    fun setValueLists(valueList: List<List<Float>>, items: List<String>) {
        this.valueList = valueList
        this.items = items
        if (valueList.isEmpty() || items.isEmpty()) {
            return
        } else {
            setValues(valueList[0],items[0])
            initPopupWindow()
        }
    }

    fun setValues(values: List<Float>,itemText:String? = null) {
        if (values.isEmpty()) {
            return
        }
        val tag = itemText ?: mTag
        average_bar_chart.setValues(values)
        var average = ceil(values.average())
        var lastValue = values[values.size - 1]
        if (lastValue > average) {
            tv_tip.text = "${context.getString(R.string.sdk_report_last_7_time_tip_head)} $tag ${
                context.getString(R.string.sdk_report_last_7_time_tip_foot_1)
            }"
            iv_arrow.setImageDrawable(mValueBiggerIcon)
        } else if (lastValue < average) {
            tv_tip.text = "${context.getString(R.string.sdk_report_last_7_time_tip_head)} $tag ${
                context.getString(R.string.sdk_report_last_7_time_tip_foot_2)
            }"
            iv_arrow.setImageDrawable(mValueSmallerIcon)
        } else {
            tv_tip.text = "${context.getString(R.string.sdk_report_last_7_time_tip_head)} $tag ${
                context.getString(R.string.sdk_report_last_7_time_tip_foot_3)
            }"
            iv_arrow.setImageDrawable(mValueEqualIcon)
        }

    }

    fun setTipBg(color: Int) {
        (ll_tip_bg.background as GradientDrawable).setColor(color)
    }

    fun setTipTextColor(color: Int) {
        tv_tip.setTextColor(color)
    }

    fun setAverageInt(flag: Boolean) {
        this.mIsAverageInt = flag
        initView()
    }

    fun setUrl(url: String) {
        this.mInfoUrl = url
        initView()
    }

    fun setLevels(levels: List<Level>) {
        this.mLevels = levels
        initView()
    }

    fun showLevelOnly(showLevelOnly:Boolean){
        this.mShowLevelOnly = showLevelOnly
        initView()
    }

    fun setAverageLineColor(color:Int){
        this.mAverageLineColor = color
        initView()
    }

    fun setOnInfoClickListener(listener:()->Unit){
        this.onInfoClickListener = listener
    }

    data class Level(
        val percentage: Float = 0f,
        val levelText: String = ""
    )
}