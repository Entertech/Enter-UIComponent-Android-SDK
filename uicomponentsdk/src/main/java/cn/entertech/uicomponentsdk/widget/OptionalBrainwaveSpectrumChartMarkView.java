package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.entertech.uicomponentsdk.R;

public class OptionalBrainwaveSpectrumChartMarkView extends MarkerView {

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
    private List<TextView> tvValues = new ArrayList<>();
    private List<LinearLayout> llMarks = new ArrayList<>();

    public OptionalBrainwaveSpectrumChartMarkView(Context context, int[] iconColors, int valueColor, int divideLineColor, int markTitleColor, String[] markTitles) {
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
        tvValues.add(tvValue1);
        tvValues.add(tvValue2);
        tvValues.add(tvValue3);
        tvValues.add(tvValue4);
        tvValues.add(tvValue5);
        llMarks.add((LinearLayout) findViewById(R.id.ll_mark_1));
        llMarks.add((LinearLayout) findViewById(R.id.ll_mark_2));
        llMarks.add((LinearLayout) findViewById(R.id.ll_mark_3));
        llMarks.add((LinearLayout) findViewById(R.id.ll_mark_4));
        llMarks.add((LinearLayout) findViewById(R.id.ll_mark_5));
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
            int maxLabel = 0;
            for (int j = 0; j < llMarks.size(); j++) {
                llMarks.get(j).setVisibility(View.GONE);
                llMarks.get(j).getChildAt(1).setVisibility(VISIBLE);
            }
            for (int i = 0; i < dataSets.size(); i++) {
                LineDataSet dataSet = (LineDataSet) dataSets.get(i);
                String label = dataSet.getLabel();
                int labelInt = Integer.parseInt(label);
                if (labelInt >= maxLabel){
                    maxLabel = labelInt;
                }
                llMarks.get(labelInt).setVisibility(View.VISIBLE);
                List<Entry> entry = dataSet.getValues();
                for (int j = 0; j < entry.size(); j++) {
                    if (e.getX() == entry.get(j).getX()) {
                        tvValues.get(labelInt).setText(Math.round(entry.get(j).getY())+"");
                    }
                }
            }
            if (maxLabel != 4){
                llMarks.get(maxLabel).getChildAt(1).setVisibility(View.GONE);
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
