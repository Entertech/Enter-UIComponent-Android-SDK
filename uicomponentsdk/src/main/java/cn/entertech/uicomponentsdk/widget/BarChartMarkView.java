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
import cn.entertech.uicomponentsdk.report.ReportBarChartCard;
import cn.entertech.uicomponentsdk.report.ReportCandleStickChartCard;
import cn.entertech.uicomponentsdk.utils.TimeUtils;

public class BarChartMarkView extends MarkerView {

    private final TextView tvValue;
    private final TextView tvMarkTitle;
    private final LinearLayout llBg;
    private final TextView tvDate;
    private final TextView tvUnit;
    private final TextView tvLevel;
    private float yOffset;

    public BarChartMarkView(Context context, String markText) {
        super(context, R.layout.layout_markview_bar);
        tvValue = findViewById(R.id.tv_value);
        tvDate = findViewById(R.id.tv_date);
        tvUnit = findViewById(R.id.tv_unit);
        tvLevel = findViewById(R.id.tv_level);
        llBg = findViewById(R.id.ll_bg);
        tvMarkTitle = findViewById(R.id.tv_title);
        tvMarkTitle.setText(markText);
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
        ReportBarChartCard.BarSourceData candleSourceData = (ReportBarChartCard.BarSourceData) e.getData();
        long startTimeMs = TimeUtils.getStringToDate(candleSourceData.getDate(), "yyyy-MM-dd");
        int value = (int)candleSourceData.getValue();
        tvValue.setText(value+"");
        tvDate.setText(TimeUtils.getFormatTime(startTimeMs,"MMM dd,yyyy"));
        if (value >= 0 && value < 29) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_low));
        } else if (value >= 30 && value < 69) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_nor));
        } else {
            tvLevel.setText(getContext().getString(R.string.sdk_report_high));
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
