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
import cn.entertech.uicomponentsdk.report.TrendPressureChart;
import cn.entertech.uicomponentsdk.utils.TimeUtils;

public class PressureTrendChartMarkView extends MarkerView {

    private final TextView tvValue;
    private final TextView tvMarkTitle;
    private final LinearLayout llBg;
    private final TextView tvDate;
    private final TextView tvUnit;
    private final TextView tvLevel;
    private final String cycle;
    private float yOffset;

    public PressureTrendChartMarkView(Context context, String markText,String cycle) {
        super(context, R.layout.layout_session_common_markview);
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
        TrendPressureChart.LineSourceData lineSourceData = (TrendPressureChart.LineSourceData) e.getData();
        float floatValue = lineSourceData.getValue();
        if (floatValue >= 0 && floatValue < 25) {
            tvValue.setText(getContext().getString(R.string.pressure_level_low));
        } else if (floatValue >= 25 && floatValue < 50) {
            tvValue.setText(getContext().getString(R.string.pressure_level_normal));
        } else if (floatValue >= 50 && floatValue < 75) {
            tvValue.setText(getContext().getString(R.string.pressure_level_elevated));
        } else {
            tvValue.setText(getContext().getString(R.string.pressure_level_high));
        }
        tvLevel.setVisibility(View.GONE);
        tvUnit.setVisibility(View.GONE);
        if (floatValue >= 0 && floatValue < 29) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_low));
        } else if (floatValue >= 30 && floatValue < 69) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_nor));
        } else {
            tvLevel.setText(getContext().getString(R.string.sdk_report_high));
        }
        if ("month".equals(cycle)){
            long date = TimeUtils.getStringToDate(lineSourceData.getDate(), "yyyy-MM-dd");
            tvDate.setText(TimeUtils.getFormatTime(date, "MMM dd, yyyy"));
        }else{
            long date = TimeUtils.getStringToDate(lineSourceData.getDate(), "yyyy-MM");
            tvDate.setText(TimeUtils.getFormatTime(date, "MMM, yyyy"));
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
