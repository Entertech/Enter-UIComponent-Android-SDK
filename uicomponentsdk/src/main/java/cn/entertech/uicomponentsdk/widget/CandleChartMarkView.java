package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.report.ReportCandleStickChartCard;

public class CandleChartMarkView extends MarkerView {

    private final TextView tvAverage;
    private final TextView tvRange;
    private final TextView tvDate;
    private float yOffset;

    public CandleChartMarkView(Context context, int color, String markText) {
        super(context, R.layout.layout_markview_candle);
        tvAverage = findViewById(R.id.tv_average);
//        setTextViewColor(tvColor, color);
        tvRange = findViewById(R.id.tv_range);
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
        ReportCandleStickChartCard.CandleSourceData candleSourceData = (ReportCandleStickChartCard.CandleSourceData) e.getData();
        tvRange.setText(candleSourceData.getMin() + "-" + candleSourceData.getMax());
        tvAverage.setText(candleSourceData.getAverage() + "");
        tvDate.setText(candleSourceData.getDate());
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
