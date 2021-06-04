package cn.entertech.uicomponentsdk.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewFeature;

import java.io.ByteArrayInputStream;

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

    public static final String INTERCEPT_URL_CHAT_WIDGET = "https://static.parastorage.com/services/chat-widget/1.2028.0/chat-widget.bundle.min.js";
    public static final String INTERCEPT_URL_COMMENT = "https://static.parastorage.com/services/communities-blog-viewer-app/1.1232.0/wix-comments-controller.bundle.min.js";

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
                jsLoad();
                if (null != mOnFinishListener) {
                    mOnFinishListener.onFinish();
                }
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (INTERCEPT_URL_CHAT_WIDGET.equals(request.getUrl().toString()) || INTERCEPT_URL_COMMENT.equals(request.getUrl().toString())) {
                    String response = "";
                    WebResourceResponse webResourceResponse = new WebResourceResponse("text/html", "utf-8", new ByteArrayInputStream(response.getBytes()));
                    return webResourceResponse;
                } else {
                    return super.shouldInterceptRequest(view, request);
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

    private void jsLoad() {
        String script = "(function() {" +
                "var holder = document.getElementById(\"SITE_HEADER-placeholder\");\n" +
                "var wrap = document.getElementById(\"SITE_HEADER_WRAPPER\");\n" +
                "var foot = document.getElementById(\"SITE_FOOTER_WRAPPER\");\n" +
                "var quickActionBar = document.getElementById(\"comp-kg7kjpyp\");\n" +
                "var content = document.getElementById(\"content-wrapper\");\n" +
                "foot.style.display = \'none\';\n" +
                "quickActionBar.style.display = \'none\';\n" +
                "wrap.style.display = \'none\';\n" +
                "if(content != null && content.firstElementChild != null && content.firstElementChild.firstElementChild !=null){\n" +
                "content.firstElementChild.firstElementChild.style.display = \'none\';\n" +
                "}\n" +
                "})()";
        mWebView.evaluateJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {

            }
        });
    }
}

