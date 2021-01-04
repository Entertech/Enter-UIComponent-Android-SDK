package cn.entertech.uicomponentsdk.utils

import android.content.Context
import android.content.Intent
import cn.entertech.uicomponentsdk.activity.WebActivity
import cn.entertech.uicomponentsdk.activity.WebActivity.WEB_TITLE
import cn.entertech.uicomponentsdk.activity.WebActivity.WEB_URL

fun toWebView(context: Context, url: String, title: String) {
    val intent = Intent(context, WebActivity::class.java).putExtra(
        WEB_TITLE,
        title
    )
    intent.putExtra(WEB_URL, url)
    context.startActivity(intent)
}