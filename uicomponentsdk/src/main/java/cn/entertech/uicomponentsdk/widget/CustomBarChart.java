package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

import cn.entertech.uicomponentsdk.utils.CustomChartTouchListener;
import cn.entertech.uicomponentsdk.utils.CustomXAxisRenderer;
import cn.entertech.uicomponentsdk.utils.CustomYAxisRenderer;
import cn.entertech.uicomponentsdk.utils.RoundedBarChartRenderer;

public class CustomBarChart extends BarChart {
    public CustomBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void init() {
        super.init();
        RoundedBarChartRenderer renderer = new RoundedBarChartRenderer(this,getAnimator(),mViewPortHandler);
        renderer.setRadius(10f);
        mRenderer = renderer;
        mXAxisRenderer = new CustomXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
        mAxisRendererLeft = new CustomYAxisRenderer(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
        mChartTouchListener = new CustomChartTouchListener(this, mViewPortHandler.getMatrixTouch(), 3f);
    }
    public void setYLimitLabelBgColor(int color){
        ((CustomYAxisRenderer)mAxisRendererLeft).setMLimitLabelBgColor(color);
    }

}
