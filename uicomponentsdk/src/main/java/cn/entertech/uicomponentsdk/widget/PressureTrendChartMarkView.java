package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.report.ReportBarChartCard;
import cn.entertech.uicomponentsdk.report.ReportPressureTrendCard;

public class PressureTrendChartMarkView extends MarkerView {

    private final TextView tvValue;
    private final TextView tvDate;
    private float yOffset;

    public PressureTrendChartMarkView(Context context, int color, String markText) {
        super(context, R.layout.layout_markview_bar);
        tvValue = findViewById(R.id.tv_value);
//        setTextViewColor(tvColor, color);
        tvDate = findViewById(R.id.tv_date);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return new MPPointF(super.getOffsetForDrawingAtPoint(posX, posY).getX(), -posY);
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
        ReportPressureTrendCard.LineSourceData lineSourceData = ( ReportPressureTrendCard.LineSourceData) e.getData();
        tvValue.setText(lineSourceData.getValue() + "");
        tvDate.setText("2022年3月" + lineSourceData.getDate() + "日");
        super.refreshContent(e, highlight);
    }
//
//    public void setMarkViewBgColor(int color) {
//        ((GradientDrawable) llBg.getBackground()).setColor(color);
//    }
//
//    public void setMarkViewValueColor(int color) {
//        tvValue.setTextColor(color);
//    }
//
//    public void setMarkTitleColor(int color) {
//        tvMarkTitle.setTextColor(color);
//    }

}
