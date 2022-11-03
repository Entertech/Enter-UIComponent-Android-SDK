package cn.entertech.uicomponentsdk.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import cn.entertech.uicomponentsdk.R
import cn.entertech.uicomponentsdk.utils.TimeUtils
import cn.entertech.uicomponentsdk.utils.getOpacityColor
import cn.entertech.wheelview.adapter.WheelAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_trend_chart_date_select.*

class TrendChartDateSelectFragment : BottomSheetDialogFragment() {
    private var mBgColor: Int = Color.parseColor("#ffffff")
    private var mTextColor: Int = Color.parseColor("#000000")
    private var mDividerColor: Int = Color.parseColor("#cccccc")
    private var mOnItemSelectListener: ((String) -> Unit)? = null
    private var mItems: List<String>? = null
    private var mFormatItems: List<String>? = null

    companion object {
        const val TAG = "TrendChartDateSelectFra"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
            }
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trend_chart_date_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        initWheelView()
    }

    var curItem: String? = null
    var tempSelectItem: String? = null
    fun initWheelView() {
        wheel_date.setCyclic(false)
        if (mItems != null) {
            wheel_date.currentItem = mItems!!.indexOf(curItem)
            wheel_date.adapter = ArrayWheelAdapter(mFormatItems!!)
            wheel_date.setOnItemSelectedListener {
                tempSelectItem = mItems!![it]
            }
            wheel_date.setDividerColor(mDividerColor)
            wheel_date.setTextColorCenter(mTextColor)
            wheel_date.setTextColorOut(getOpacityColor(mTextColor, 0.5f))
        }
        tv_confirm.setOnClickListener {
            if (tempSelectItem != null) {
                curItem = tempSelectItem
                mOnItemSelectListener?.invoke(curItem!!)
            }else{
                if (mItems != null){
                    curItem = mItems!![0]
                    mOnItemSelectListener?.invoke(curItem!!)
                }
            }
            dismiss()
        }
        tv_cancel.setOnClickListener {
            dismiss()
        }
        ll_bg.setBackgroundColor(mBgColor)
        tv_confirm.setTextColor(mTextColor)
        tv_cancel.setTextColor(mTextColor)
    }

    class ArrayWheelAdapter(var items: List<String>) : WheelAdapter<String> {
        override fun getItemsCount(): Int {
            return items.size
        }

        override fun getItem(p0: Int): String {
            return items[p0]
        }

        override fun indexOf(p0: String?): Int {
            return items.indexOf(p0)
        }
    }

    fun setItems(items: List<String>, onItemSelectListener: (String) -> Unit) {
        this.mItems = items
        mFormatItems = mItems!!.map {
            val dates = it.split("-")
            if (dates.size > 1) {
                val time = TimeUtils.getStringToDate("${dates[0]}-${dates[1]}", "yyyy-MM")
                TimeUtils.getFormatTime(time, "MMM.yyyy")
            } else {
                it
            }
        }
        this.mOnItemSelectListener = onItemSelectListener


    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        if (curItem != null && wheel_date != null) {
            wheel_date.currentItem = mItems!!.indexOf(curItem)
        }
    }

    fun setBgColor(color: Int) {
        this.mBgColor = color
    }

    fun setTextColor(color: Int) {
        this.mTextColor = color
    }

    fun setDividerColor(color: Int) {
        this.mDividerColor = color
    }
}