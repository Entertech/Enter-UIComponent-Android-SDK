package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.utils.TimeUtils;

public class SessionBrainwaveChartMarkView extends MarkerView {

    private final TextView tvValue1;
    private final TextView tvValue2;
    private final TextView tvValue3;
    private final TextView tvValue4;
    private final TextView tvValue5;
    private final LinearLayout llBg;
    private final TextView tvMarkViewTitle;
    private final TextView tvDate;
    private final TextView tvValueIcon1;
    private final TextView tvValueIcon2;
    private final TextView tvValueIcon3;
    private final TextView tvValueIcon4;
    private final TextView tvValueIcon5;

    private final TextView tvValueUnit1;
    private final TextView tvValueUnit2;
    private final TextView tvValueUnit3;
    private final TextView tvValueUnit4;
    private final TextView tvValueUnit5;
    private final String startTime;
    private List<ILineDataSet> dataSets;
    private List<TextView> tvValues = new ArrayList<>();
    private List<LinearLayout> llMarks = new ArrayList<>();

    public SessionBrainwaveChartMarkView(Context context, int[] valueColor, int divideLineColor, String startTime) {
        super(context, R.layout.layout_markview_session_brainwave);
        tvValue1 = findViewById(R.id.tv_value_1);
        tvValue2 = findViewById(R.id.tv_value_2);
        tvValue3 = findViewById(R.id.tv_value_3);
        tvValue4 = findViewById(R.id.tv_value_4);
        tvValue5 = findViewById(R.id.tv_value_5);

        tvValueIcon1 = findViewById(R.id.tv_icon_1);
        tvValueIcon2 = findViewById(R.id.tv_icon_2);
        tvValueIcon3 = findViewById(R.id.tv_icon_3);
        tvValueIcon4 = findViewById(R.id.tv_icon_4);
        tvValueIcon5 = findViewById(R.id.tv_icon_5);
        tvValueUnit1 = findViewById(R.id.tv_unit_1);
        tvValueUnit2 = findViewById(R.id.tv_unit_2);
        tvValueUnit3 = findViewById(R.id.tv_unit_3);
        tvValueUnit4 = findViewById(R.id.tv_unit_4);
        tvValueUnit5 = findViewById(R.id.tv_unit_5);
        tvMarkViewTitle = findViewById(R.id.tv_markview_title);
        tvDate = findViewById(R.id.tv_date);
        tvValue1.setTextColor(valueColor[0]);
        tvValue2.setTextColor(valueColor[1]);
        tvValue3.setTextColor(valueColor[2]);
        tvValue4.setTextColor(valueColor[3]);
        tvValue5.setTextColor(valueColor[4]);
        tvValueIcon1.setTextColor(valueColor[0]);
        tvValueIcon2.setTextColor(valueColor[1]);
        tvValueIcon3.setTextColor(valueColor[2]);
        tvValueIcon4.setTextColor(valueColor[3]);
        tvValueIcon5.setTextColor(valueColor[4]);
        tvValueUnit1.setTextColor(valueColor[0]);
        tvValueUnit2.setTextColor(valueColor[1]);
        tvValueUnit3.setTextColor(valueColor[2]);
        tvValueUnit4.setTextColor(valueColor[3]);
        tvValueUnit5.setTextColor(valueColor[4]);
        llBg = findViewById(R.id.ll_bg);
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
        this.startTime = startTime;
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
                if (labelInt >= maxLabel) {
                    maxLabel = labelInt;
                }
                llMarks.get(labelInt).setVisibility(View.VISIBLE);
                List<Entry> curEntries = dataSet.getValues();
                if (i + 1 < dataSets.size()) {
                    List<Entry> nextEntries = ((LineDataSet) dataSets.get(i + 1)).getValues();
                    for (int j = 0; j < curEntries.size(); j++) {
                        if (e.getX() == curEntries.get(j).getX()) {
                            tvValues.get(labelInt).setText(Math.round(curEntries.get(j).getY()-Math.round(nextEntries.get(j).getY())) + "");
                        }
                    }
                }else{
                    for (int j = 0; j < curEntries.size(); j++) {
                        if (e.getX() == curEntries.get(j).getX()) {
                            tvValues.get(labelInt).setText(Math.round(curEntries.get(j).getY()) + "");
                        }
                    }
                }

            }
            if (maxLabel != 4) {
                llMarks.get(maxLabel).getChildAt(3).setVisibility(View.GONE);
            }
        }
        long startTimeMs = TimeUtils.getStringToDate(startTime, "yyyy-MM-dd HH:mm:ss");
        long curPointTimeMs = startTimeMs + (long) (e.getX() * 600);
        String curDate = TimeUtils.getFormatTime(curPointTimeMs, "MMM dd, yyyy HH:mm a");
        tvDate.setText(curDate);
        super.refreshContent(e, highlight);
    }

    public void setMarkViewBgColor(int color) {
        ((GradientDrawable) llBg.getBackground()).setColor(color);
    }

    public void setTextColor(int color) {
        tvMarkViewTitle.setTextColor(color);
        tvDate.setTextColor(color);
    }

    public void setDataSets(List<ILineDataSet> dataSets) {
        this.dataSets = dataSets;
    }
}
