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

import java.util.List;

import cn.entertech.uicomponentsdk.R;

public class BrainwaveSpectrumChartMarkView extends MarkerView {

    private final TextView tvValue1;
    private final TextView tvValue2;
    private final TextView tvValue3;
    private final TextView tvValue4;
    private final TextView tvValue5;
    private final TextView tvColor1;
    private final TextView tvColor2;
    private final TextView tvColor3;
    private final TextView tvColor4;
    private final TextView tvColor5;
    private final TextView tvMarkTitle1;
    private final TextView tvMarkTitle2;
    private final TextView tvMarkTitle3;
    private final TextView tvMarkTitle4;
    private final TextView tvMarkTitle5;
    private final LinearLayout llBg;
    private List<ILineDataSet> dataSets;

    public BrainwaveSpectrumChartMarkView(Context context, int[] iconColors, int valueColor, int divideLineColor, int markTitleColor, String[] markTitles) {
        super(context, R.layout.layout_markview_brainwave_spectrum);
        tvColor1 = findViewById(R.id.tv_color_1);
        tvColor2 = findViewById(R.id.tv_color_2);
        tvColor3 = findViewById(R.id.tv_color_3);
        tvColor4 = findViewById(R.id.tv_color_4);
        tvColor5 = findViewById(R.id.tv_color_5);
        setTextViewColor(tvColor1, iconColors[0]);
        setTextViewColor(tvColor2, iconColors[1]);
        setTextViewColor(tvColor3, iconColors[2]);
        setTextViewColor(tvColor4, iconColors[3]);
        setTextViewColor(tvColor5, iconColors[4]);
        tvValue1 = findViewById(R.id.tv_value_1);
        tvValue2 = findViewById(R.id.tv_value_2);
        tvValue3 = findViewById(R.id.tv_value_3);
        tvValue4 = findViewById(R.id.tv_value_4);
        tvValue5 = findViewById(R.id.tv_value_5);
        tvValue1.setTextColor(valueColor);
        tvValue2.setTextColor(valueColor);
        tvValue3.setTextColor(valueColor);
        tvValue4.setTextColor(valueColor);
        tvValue5.setTextColor(valueColor);
        llBg = findViewById(R.id.ll_bg);
        tvMarkTitle1 = findViewById(R.id.tv_title_1);
        tvMarkTitle2 = findViewById(R.id.tv_title_2);
        tvMarkTitle3 = findViewById(R.id.tv_title_3);
        tvMarkTitle4 = findViewById(R.id.tv_title_4);
        tvMarkTitle5 = findViewById(R.id.tv_title_5);
        tvMarkTitle1.setText(markTitles[0]);
        tvMarkTitle2.setText(markTitles[1]);
        tvMarkTitle3.setText(markTitles[2]);
        tvMarkTitle4.setText(markTitles[3]);
        tvMarkTitle5.setText(markTitles[4]);
        tvMarkTitle1.setTextColor(markTitleColor);
        tvMarkTitle2.setTextColor(markTitleColor);
        tvMarkTitle3.setTextColor(markTitleColor);
        tvMarkTitle4.setTextColor(markTitleColor);
        tvMarkTitle5.setTextColor(markTitleColor);
        findViewWithTag("divideLine1").setBackgroundColor(divideLineColor);
        findViewWithTag("divideLine2").setBackgroundColor(divideLineColor);
        findViewWithTag("divideLine3").setBackgroundColor(divideLineColor);
        findViewWithTag("divideLine4").setBackgroundColor(divideLineColor);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return new MPPointF(super.getOffsetForDrawingAtPoint(posX, posY).getX(), -posY);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth() / 2, 0);
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
            if (dataSets == null) {
                return;
            }
            List<Entry> entry1 = ((LineDataSet) dataSets.get(0)).getValues();
            List<Entry> entry2 = ((LineDataSet) dataSets.get(1)).getValues();
            List<Entry> entry3 = ((LineDataSet) dataSets.get(2)).getValues();
            List<Entry> entry4 = ((LineDataSet) dataSets.get(3)).getValues();
            List<Entry> entry5 = ((LineDataSet) dataSets.get(4)).getValues();
            for (int i = 0; i < entry1.size(); i++) {
                if (e.getX() == entry1.get(i).getX()) {
                    int value1 = Math.round(entry1.get(i).getY() - entry2.get(i).getY());
                    int value2 = Math.round(entry2.get(i).getY() - entry3.get(i).getY());
                    int value3 = Math.round(entry3.get(i).getY() - entry4.get(i).getY());
                    int value4 = Math.round(entry4.get(i).getY() - entry5.get(i).getY());
                    int value5 = 100 - value1 - value2 - value3 - value4;
                    tvValue1.setText(Utils.formatNumber(value1, 0, true) + "%");
                    tvValue2.setText(Utils.formatNumber(value2, 0, true) + "%");
                    tvValue3.setText(Utils.formatNumber(value3, 0, true) + "%");
                    tvValue4.setText(Utils.formatNumber(value4, 0, true) + "%");
                    tvValue5.setText(Utils.formatNumber(value5, 0, true) + "%");
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
