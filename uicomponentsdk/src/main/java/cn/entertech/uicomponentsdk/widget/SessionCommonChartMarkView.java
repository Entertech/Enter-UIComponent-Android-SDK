package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.utils.TimeUtils;

public class SessionCommonChartMarkView extends MarkerView {

    private final TextView tvValue;
    private final TextView tvMarkTitle;
    private final LinearLayout llBg;
    private final TextView tvDate;
    private final TextView tvUnit;
    private final TextView tvLevel;
    private final String mMarkText;
    private final boolean hasSecondLine;
    private String startTime;
    private float yOffset;

    public SessionCommonChartMarkView(Context context, String markText, String startTime,boolean hasSecondLine) {
        super(context, R.layout.layout_session_common_markview);
        tvValue = findViewById(R.id.tv_value);
        tvDate = findViewById(R.id.tv_date);
        tvUnit = findViewById(R.id.tv_unit);
        tvLevel = findViewById(R.id.tv_level);
        llBg = findViewById(R.id.ll_bg);
        tvMarkTitle = findViewById(R.id.tv_title);
        mMarkText = markText;
        tvMarkTitle.setText(markText);
        this.hasSecondLine = hasSecondLine;
        this.startTime = startTime;
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
        long startTimeMs = TimeUtils.getStringToDate(startTime, "yyyy-MM-dd HH:mm:ss");
        long curPointTimeMs = startTimeMs + (long) (e.getX() * 600);
        String value = Utils.formatNumber(e.getY(), 0, true);
        double doubleValue = Double.parseDouble(value);
        if (hasSecondLine) {//和谐度
            if (e.getData() != null ) {
                double coherenceFlag = (double)e.getData();
                if (coherenceFlag >0.0){
                    tvValue.setText(R.string.chart_coherent);
                }else{
                    tvValue.setText(R.string.chart_incoherent);
                }
            } else {
                tvValue.setText(R.string.chart_incoherent);
            }
            tvLevel.setVisibility(View.GONE);
            tvUnit.setVisibility(View.GONE);
            tvMarkTitle.setText(R.string.chart_state);
        } else {
            tvValue.setText(value);
            tvMarkTitle.setText(mMarkText);
        }
        if (doubleValue >= 0 && doubleValue < 29) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_low));
        } else if (doubleValue >= 30 && doubleValue < 69) {
            tvLevel.setText(getContext().getString(R.string.sdk_report_nor));
        } else {
            tvLevel.setText(getContext().getString(R.string.sdk_report_high));
        }
        tvDate.setText(TimeUtils.getFormatTime(curPointTimeMs, "MMM dd, yyyy HH:mm a"));
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
