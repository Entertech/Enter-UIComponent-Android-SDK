package cn.entertech.uicomponentsdk.realtime;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

public class BrainWaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private int mPointBgColor = Color.parseColor("#11152E");
    private float mLineWidth;
    private float mRightPadding;
    private float mLeftPadding;
    private int mGridLineCount = 4;
    private int mGridLineColor = Color.parseColor("#383838");
    private int mYAxisColor = Color.parseColor("#383838");
    private int mLineColor = Color.parseColor("#ff6682");
    private int mBgColor = Color.parseColor("#f2f4fb");
    private Paint mCruvePaint;
    private List<Double> mSourceData = new ArrayList<>();
    List<Double> drawData = new ArrayList<>();
    List<Double> sampleData = new ArrayList<>();
    private boolean isViewActivity;
    private SurfaceHolder mSurfaceHolder;
    public static int BRAIN_QUEUE_LENGTH = 200;
    public static int BRAIN_BUFFER_LENGTH = 50;
    private Paint mStartLinePaint;
    private Paint mGridLinePaint;
    private Paint mBgPaint;
    private boolean isShowSampleData = false;
    private int mMaxValue = 300;

    public BrainWaveSurfaceView(Context context) {
        this(context, null);
    }

    public BrainWaveSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrainWaveSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrainWaveSurfaceView);
        if (typedArray != null) {
            mLineColor = typedArray.getColor(R.styleable.BrainWaveSurfaceView_bwsf_lineColor, mLineColor);
            mBgColor = typedArray.getColor(R.styleable.BrainWaveSurfaceView_bwsf_bgColor, mBgColor);
            mYAxisColor = typedArray.getColor(R.styleable.BrainWaveSurfaceView_bwsf_yAxisColor, mYAxisColor);
            mGridLineColor = typedArray.getColor(R.styleable.BrainWaveSurfaceView_bwsf_gridLineColor, mGridLineColor);
            mGridLineCount = typedArray.getInteger(R.styleable.BrainWaveSurfaceView_bwsf_gridLineCount, mGridLineCount);
            mLeftPadding = typedArray.getDimension(R.styleable.BrainWaveSurfaceView_bwsf_leftPadding, ScreenUtil.dip2px(context, 5));
            mRightPadding = typedArray.getDimension(R.styleable.BrainWaveSurfaceView_bwsf_rightPadding, ScreenUtil.dip2px(context, 5));
            mLineWidth = typedArray.getDimension(R.styleable.BrainWaveSurfaceView_bwsf_lineWidth, 3);
            mMaxValue = typedArray.getInteger(R.styleable.BrainWaveSurfaceView_bwsf_maxValue, mMaxValue);
            mPointBgColor = typedArray.getColor(R.styleable.BrainWaveSurfaceView_bwsf_pointBgColor, mPointBgColor);
        }
        initPaint();
    }

    private void initPaint() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mCruvePaint = new Paint();
        mCruvePaint.setDither(true);
        mCruvePaint.setStyle(Paint.Style.STROKE);
        mCruvePaint.setAntiAlias(true);
        mCruvePaint.setStrokeWidth(mLineWidth);
        CornerPathEffect pathEffect = new CornerPathEffect(25);
        mCruvePaint.setPathEffect(pathEffect);
        mCruvePaint.setColor(mLineColor);
        mStartLinePaint = new Paint();
        mStartLinePaint.setStyle(Paint.Style.STROKE);
        mStartLinePaint.setColor(mYAxisColor);
        mStartLinePaint.setStrokeWidth(3);

        mGridLinePaint = new Paint();
        mGridLinePaint.setStyle(Paint.Style.STROKE);
        mGridLinePaint.setColor(mGridLineColor);
        mGridLinePaint.setStrokeWidth(3);
        initData();
        mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
//        this.setZOrderOnTop(true);
////        this.setZOrderMediaOverlay(true);
//        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public synchronized void setData(ArrayList<Double> data) {
        if (data == null) {
            return;
        }
        List<Double> simpleData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (i % 10 == 0) {
                simpleData.add(data.get(i));
            }
        }
        this.mSourceData.addAll(simpleData);
        simpleData.clear();
        if (mSourceData.size() > BRAIN_BUFFER_LENGTH) {
            for (int i = 0; i < mSourceData.size() - BRAIN_BUFFER_LENGTH; i++) {
                mSourceData.remove(0);
            }
        }
    }


    public synchronized void setData(double data) {
        this.mSourceData.add(data);
        if (mSourceData.size() > BRAIN_BUFFER_LENGTH) {
            for (int i = 0; i < mSourceData.size() - BRAIN_BUFFER_LENGTH; i++) {
                mSourceData.remove(0);
            }
        }
    }


    private void initData() {
        for (int i = 0; i < BRAIN_QUEUE_LENGTH; i++) {
            drawData.add(1.0);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isViewActivity = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isViewActivity = false;
    }

    @Override
    public void run() {
        while (isViewActivity) {
            draw();
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void onDrawBg(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
//        canvas.drawColor(mBgColor,PorterDuff.Mode.CLEAR);
        float lineOffset = (getWidth() - (mLeftPadding + mRightPadding)) / 4;
        for (int i = 0; i < (mGridLineCount + 1); i++) {
            if (i == 0) {
                canvas.drawLine(mLeftPadding, 0, mLeftPadding, getHeight(), mStartLinePaint);
            } else {
                //绘制长度为4的实线后再绘制长度为4的空白区域，0位间隔
                mGridLinePaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));
                canvas.drawLine(lineOffset * i, 0, lineOffset * i, getHeight(), mGridLinePaint);
            }
        }
    }

    public void onDrawBrainWave(Canvas canvas) {
        float pointOffset = getWidth() * 1f / (drawData.size() - 1);
        dealData();
        //获得canvas对象
        canvas.translate(mLeftPadding, getHeight() / 2);
        Path path = new Path();
//        Log.d("####","draw data is "+drawData.toString());
        for (int i = 0; i < drawData.size(); i++) {
            if (i == 0)
                path.moveTo(i * pointOffset, (float) (-(drawData.get(i))));
            path.lineTo(i * pointOffset, (float) (-(drawData.get(i))));

        }
        canvas.drawPath(path, mCruvePaint);

    }

    public void onDrawSampleData(Canvas canvas) {
        float pointOffset = getWidth() * 1f / (sampleData.size() - 1);
        //获得canvas对象
        canvas.translate(mLeftPadding, getHeight() / 2);
        Path path = new Path();
        for (int i = 0; i < sampleData.size(); i++) {
            if (i == 0)
                path.moveTo(i * pointOffset, (float) (-(sampleData.get(i))));
            path.lineTo(i * pointOffset, (float) (-(sampleData.get(i))));

        }
        canvas.drawPath(path, mCruvePaint);
    }

    private synchronized void draw() {
        Canvas mCanvas = null;
        try {
            mCanvas = mSurfaceHolder.lockCanvas(null);
//            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            onDrawBg(mCanvas);
            if (isShowSampleData) {
                onDrawSampleData(mCanvas);
            } else {
                onDrawBrainWave(mCanvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    public void dealData() {
        if (mSourceData.size() == 0) {
            drawData.add(1.0);
            drawData.remove(0);
        } else {
            float time = mMaxValue / ((getHeight() / 2));
            if (mSourceData.get(0) == 0.0) {
                drawData.add(1.0);
                mSourceData.remove(0);
                drawData.remove(0);
            } else {
                drawData.add(mSourceData.get(0) / time);
                mSourceData.remove(0);
                drawData.remove(0);
            }
        }
    }

    public void setSampleData(List<Double> sampleData) {
        this.sampleData = sampleData;
        this.isShowSampleData = true;
    }

    public void setLineColor(int color){
        this.mLineColor = color;
        mCruvePaint.setColor(mLineColor);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color){
        this.mBgColor = color;
        mBgPaint.setColor(mBgColor);
        invalidate();
    }

    public void hideSampleData() {
        this.isShowSampleData = false;
    }
}
