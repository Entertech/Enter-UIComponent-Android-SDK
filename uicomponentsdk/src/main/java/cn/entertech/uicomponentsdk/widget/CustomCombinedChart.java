package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;

import cn.entertech.uicomponentsdk.utils.CustomChartTouchListener;
import cn.entertech.uicomponentsdk.utils.CustomCombinedChartRenderer;
import cn.entertech.uicomponentsdk.utils.CustomXAxisRenderer;
import cn.entertech.uicomponentsdk.utils.CustomYAxisRenderer;

public class CustomCombinedChart extends CombinedChart {
    public CustomCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void init() {
        super.init();
        mRenderer = new CustomCombinedChartRenderer(this,getAnimator(),mViewPortHandler);
        mXAxisRenderer = new CustomXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
        mAxisRendererLeft = new CustomYAxisRenderer(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
        mChartTouchListener = new CustomChartTouchListener(this, mViewPortHandler.getMatrixTouch(), 3f);

    }
    public void setYLimitLabelBgColor(int color){
        ((CustomYAxisRenderer)mAxisRendererLeft).setMLimitLabelBgColor(color);
    }

}
