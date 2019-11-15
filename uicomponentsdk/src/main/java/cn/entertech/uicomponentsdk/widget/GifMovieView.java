package cn.entertech.uicomponentsdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class GifMovieView extends View {
    private Context mContext;
    private Movie mMovie;

    private long mMoviestart;

    public GifMovieView(Context context, AttributeSet attributeSet, int def) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public GifMovieView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet,0);
    }

    public GifMovieView(Context context, InputStream stream) {
        this(context, null, 0);
        mMovie = Movie.decodeStream(stream);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null){
            canvas.drawColor(Color.TRANSPARENT);
            super.onDraw(canvas);
            final long now = SystemClock.uptimeMillis();

            if (mMoviestart == 0) {
                mMoviestart = now;
            }

            final int relTime = (int) ((now - mMoviestart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas, 0, 0);
            this.invalidate();
        }
    }

    public void loadGif(String assetFileName) {
        InputStream stream = null;
        try {
            stream = mContext.getAssets().open(assetFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMovie = Movie.decodeStream(stream);
        invalidate();
    }

}
