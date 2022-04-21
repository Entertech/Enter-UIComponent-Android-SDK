package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;

import cn.entertech.uicomponentsdk.utils.CustomChartTouchListener;
import cn.entertech.uicomponentsdk.utils.CustomXAxisRenderer;
import cn.entertech.uicomponentsdk.utils.CustomYAxisRenderer;

public class CustomLineChart extends LineChart {
    private int[] gridBackgroundColors;

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

    public void setYLimitLabelBgColor(int color) {
        ((CustomYAxisRenderer) mAxisRendererLeft).setMLimitLabelBgColor(color);
    }

    protected void drawGridBackground(Canvas c) {
        if (mDrawGridBackground) {
            if (gridBackgroundColors != null) {
                mGridBackgroundPaint.setShader(new LinearGradient(0f,
                        mViewPortHandler.getContentRect().top, 0f,
                        mViewPortHandler.getContentRect().bottom,
                        gridBackgroundColors,
                        null,
                        Shader.TileMode.MIRROR));
            } else {
                mGridBackgroundPaint.setShader(null);
            }
            c.drawRect(mViewPortHandler.getContentRect(), mGridBackgroundPaint);
        }

        if (mDrawBorders) {
            c.drawRect(mViewPortHandler.getContentRect(), mBorderPaint);
        }
    }

    public void setGridBackgroundColors(int[] colors) {
        this.gridBackgroundColors = colors;
    }


}
