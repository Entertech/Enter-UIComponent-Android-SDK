package cn.entertech.uicomponentsdk.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewFeature;

import cn.entertech.uicomponentsdk.R;
import cn.entertech.uicomponentsdk.utils.NetworkState;

/**
 * Created by EnterTech on 2017/1/4.
 */

public class WebActivity extends AppCompatActivity {

    //    private XWalkView xWalkWebView;
    protected WebView mWebView;
    private ProgressBar mProgressBar;

    public static final String WEB_TITLE = "webTitle";
    public static final String WEB_URL = "url";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.defaultThemeColor));
        setContentView(R.layout.activity_web);
        String text = getIntent().getStringExtra(WEB_TITLE);
        TextView title = (TextView) findViewById(R.id.web_title);
        title.setText(text);

        load();
    }


    protected void load() {
        if (-1 == NetworkState.getConnectedType(this)) {
            findViewById(R.id.web_nonetwork).setVisibility(View.VISIBLE);
            findViewById(R.id.web_progress).setVisibility(View.GONE);
            return;
        }

//        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//        xWalkWebView = (XWalkView) findViewById(R.id.web_xwalk);
        mWebView = (WebView) findViewById(R.id.web_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.web_progress);

        findViewById(R.id.web_nonetwork).setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        final String url = getIntent().getStringExtra(WEB_URL);
        mWebView.setVisibility(View.VISIBLE);
//        xWalkWebView.setVisibility(View.GONE);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (null != mOnFinishListener) {
                    mOnFinishListener.onFinish();
                }
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mWebView.getSettings().setForceDark(WebSettings.FORCE_DARK_AUTO);
        }
        mWebView.loadUrl(url);

//        //暂时注释xWalkWebView，适应很差
//        if (url.equals(Constants.ME_MARKET)) {
//            mWebView.setVisibility(View.VISIBLE);
//            xWalkWebView.setVisibility(View.GONE);
//            mWebView.setWebChromeClient(new WebChromeClient() {
//                @Override
//                public void onProgressChanged(WebView view, int newProgress) {
//                    super.onProgressChanged(view, newProgress);
//                    mProgressBar.setProgress(newProgress);
//                    if (newProgress >= 100) {
//                        mProgressBar.setVisibility(View.GONE);
//                    }
//                }
//
//            });
//            mWebView.setWebViewClient(new WebViewClient());
//            mWebView.getSettings().setJavaScriptEnabled(true);
//            mWebView.loadUrl(url);
//        } else {
//            mWebView.setVisibility(View.GONE);
//            xWalkWebView.setResourceClient(new XWalkResourceClient(xWalkWebView) {
//                @Override
//                public void onProgressChanged(XWalkView view, int progressInPercent) {
//                    super.onProgressChanged(view, progressInPercent);
//                    mProgressBar.setProgress(progressInPercent);
//                    if (progressInPercent >= 100) {
//                        mProgressBar.setVisibility(View.GONE);
//                    }
//                }
//            });
//            xWalkWebView.setVisibility(View.VISIBLE);
//        }
    }

//    @Override
//    protected void onXWalkReady() {
//        final String url = getIntent().getStringExtra(ExtraKey.EXTRA_URL);
//        if (!url.equals(Constants.ME_MARKET)) {
//            xWalkWebView.loadUrl(url);
//        }
//    }

    public void onBack(View view) {
        finish();
    }

    public void onReload(View view) {
        load();
    }

    public interface OnFinishListener {
        void onFinish();
    }

    private OnFinishListener mOnFinishListener;

    protected void setOnFinishListener(OnFinishListener listener) {
        mOnFinishListener = listener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
