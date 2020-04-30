package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.entertech.uicomponentsdk.R;

public class AAndRLineChartMarkView extends MarkerView {

    private final TextView tvValue1;
    private final TextView tvValue2;
    private final TextView tvColor1;
    private final TextView tvColor2;
    private final TextView tvMarkTitle1;
    private final TextView tvMarkTitle2;
    private final LinearLayout llBg;
    private List<ILineDataSet> dataSets;

    public AAndRLineChartMarkView(Context context, int color1, int color2, int divideLineColor,String markTitle1, String markTitle2) {
        super(context, R.layout.layout_markview_a_and_r);
        tvColor1 = findViewById(R.id.tv_color_1);
        tvColor2 = findViewById(R.id.tv_color_2);
        setTextViewColor(tvColor1, color1);
        setTextViewColor(tvColor2, color2);
        tvValue1 = findViewById(R.id.tv_value_1);
        tvValue2 = findViewById(R.id.tv_value_2);
        llBg = findViewById(R.id.ll_bg);
        tvMarkTitle1 = findViewById(R.id.tv_title_1);
        tvMarkTitle2 = findViewById(R.id.tv_title_2);
        tvMarkTitle1.setText(markTitle1);
        tvMarkTitle2.setText(markTitle2);
        findViewById(R.id.view_divide_line).setBackgroundColor(divideLineColor);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return new MPPointF(super.getOffsetForDrawingAtPoint(posX, posY).getX(), -posY);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth() / 2f, 0);
    }

    public void setTextViewColor(TextView textView, int color) {
        ((GradientDrawable) (textView.getBackground())).setColor(color);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvValue1.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            if (dataSets == null){
                return;
            }
            List<Entry> attentionEntrys = ((LineDataSet)dataSets.get(0)).getValues();
            List<Entry> relaxationEntrys = ((LineDataSet)dataSets.get(1)).getValues();
            for (int i = 0; i < attentionEntrys.size(); i++) {
                if (e.getX() == attentionEntrys.get(i).getX()) {
                    tvValue1.setText(Utils.formatNumber(attentionEntrys.get(i).getY()-120, 0, true));
                    tvValue2.setText(Utils.formatNumber(relaxationEntrys.get(i).getY(), 0, true));
                }
            }
        }
        super.refreshContent(e, highlight);
    }

    public void setMarkViewBgColor(int color) {
        ((GradientDrawable) llBg.getBackground()).setColor(color);
    }

    public void setMarkViewValueColor(int color) {
        tvValue1.setTextColor(color);
        tvValue2.setTextColor(color);
    }

    public void setMarkTitleColor(int color) {
        tvMarkTitle1.setTextColor(color);
        tvMarkTitle2.setTextColor(color);
    }

    public void setDataSets(List<ILineDataSet> dataSets) {
        this.dataSets = dataSets;
    }
}
