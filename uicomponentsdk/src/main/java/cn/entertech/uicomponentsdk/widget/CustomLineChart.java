package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;

import cn.entertech.uicomponentsdk.utils.CustomXAxisRenderer;

public class CustomLineChart extends LineChart {
    public CustomLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void init() {
        super.init();
        mXAxisRenderer = new CustomXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
    }
}
