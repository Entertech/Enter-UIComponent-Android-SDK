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

public class LineChartMarkView extends MarkerView {

    private final TextView tvValue;
    private final TextView tvColor;
    private final TextView tvMarkTitle;
    private final LinearLayout llBg;
    private float yOffset;

    public LineChartMarkView(Context context, int color, String markText) {
        super(context, R.layout.layout_markview);
        tvColor = findViewById(R.id.tv_color);
        setTextViewColor(tvColor, color);
        tvValue = findViewById(R.id.tv_value);
        llBg = findViewById(R.id.ll_bg);
        tvMarkTitle = findViewById(R.id.tv_text);
        tvMarkTitle.setText(markText);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return new MPPointF(super.getOffsetForDrawingAtPoint(posX, posY).getX(), -posY+yOffset);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth() / 2, 0);
    }

    public void setYOffset(float yOffset){
        this.yOffset = yOffset;
    }
    public void setTextViewColor(TextView textView, int color) {
        ((GradientDrawable) (textView.getBackground())).setColor(color);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvValue.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            tvValue.setText(Utils.formatNumber(e.getY(), 0, true));
        }
        super.refreshContent(e, highlight);
    }

    public void setMarkViewBgColor(int color) {
        ((GradientDrawable) llBg.getBackground()).setColor(color);
    }

    public void setMarkViewValueColor(int color) {
        tvValue.setTextColor(color);
    }

    public void setMarkTitleColor(int color) {
        tvMarkTitle.setTextColor(color);
    }

}
