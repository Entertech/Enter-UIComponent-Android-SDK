package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.report.TrendCommonCandleChart;
import cn.entertech.uicomponentsdk.utils.TimeUtils;

public class CandleChartMarkView extends MarkerView {

    private final TextView tvValue;
    private final TextView tvMarkTitle;
    private final LinearLayout llBg;
    private final TextView tvDate;
    private final TextView tvUnit;
    private final TextView tvLevel;
    private final String cycle;
    private float yOffset;

    public CandleChartMarkView(Context context, String markText,String cycle) {
        super(context, R.layout.layout_markview_candle);
        tvValue = findViewById(R.id.tv_value);
        tvDate = findViewById(R.id.tv_date);
        tvUnit = findViewById(R.id.tv_unit);
        tvLevel = findViewById(R.id.tv_level);
        llBg = findViewById(R.id.ll_bg);
        tvMarkTitle = findViewById(R.id.tv_title);
        tvMarkTitle.setText(markText);
        this.cycle = cycle;
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return new MPPointF(super.getOffsetForDrawingAtPoint(posX, posY).getX(), -posY + yOffset);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth() / 2, 0);
    }

    public void setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public void setTextViewColor(TextView textView, int color) {
        ((GradientDrawable) (textView.getBackground())).setColor(color);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        TrendCommonCandleChart.CandleSourceData candleSourceData = (TrendCommonCandleChart.CandleSourceData) e.getData();
        int value = (int)candleSourceData.getAverage();
        tvValue.setText(value+"");
        if (value >= 0 && value < 29) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_low));
        } else if (value >= 30 && value < 69) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_nor));
        } else {
            tvLevel.setText(getContext().getString(R.string.sdk_report_high));
        }
        if ("month".equals(cycle)){
            long startTimeMs = TimeUtils.getStringToDate(candleSourceData.getDate(), "yyyy-MM-dd");
            tvDate.setText(TimeUtils.getFormatTime(startTimeMs,"MMM dd, yyyy"));
        }else{
            long startTimeMs = TimeUtils.getStringToDate(candleSourceData.getDate(), "yyyy-MM");
            tvDate.setText(TimeUtils.getFormatTime(startTimeMs,"MMM, yyyy"));
        }
        super.refreshContent(e, highlight);
    }

    public void setMarkViewBgColor(int color) {
        ((GradientDrawable) llBg.getBackground()).setColor(color);
    }

    public void setMainColor(int color) {
        tvValue.setTextColor(color);
    }

    public void setTextColor(int color) {
        tvMarkTitle.setTextColor(color);
        tvDate.setTextColor(color);
        tvUnit.setTextColor(color);
    }

    public void setShowLevel(boolean showLevel, int levelTextColor, int levelBgColor) {
        if (showLevel) {
            tvLevel.setTextColor(levelTextColor);
            GradientDrawable bg = (GradientDrawable) tvLevel.getBackground();
            bg.setColor(levelBgColor);
            tvUnit.setVisibility(View.GONE);
            tvLevel.setVisibility(View.VISIBLE);
        } else {
            tvUnit.setVisibility(View.VISIBLE);
            tvLevel.setVisibility(View.GONE);
        }
    }

    public void setUnit(String unit) {
        tvUnit.setText(unit);
    }

}
