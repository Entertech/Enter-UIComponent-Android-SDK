package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;

import cn.entertech.uicomponentsdk.utils.CustomChartTouchListener;
import cn.entertech.uicomponentsdk.utils.CustomXAxisRenderer;
import cn.entertech.uicomponentsdk.utils.CustomYAxisRenderer;

public class CustomLineChart extends LineChart {
    public CustomLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void init() {
        super.init();
        mXAxisRenderer = new CustomXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
        mAxisRendererLeft = new CustomYAxisRenderer(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
        mChartTouchListener = new CustomChartTouchListener(this, mViewPortHandler.getMatrixTouch(), 3f);

    }
    public void setYLimitLabelBgColor(int color){
        ((CustomYAxisRenderer)mAxisRendererLeft).setMLimitLabelBgColor(color);
    }

}
