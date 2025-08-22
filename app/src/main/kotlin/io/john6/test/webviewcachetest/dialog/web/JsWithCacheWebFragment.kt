package io.john6.test.webviewcachetest.dialog.web

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import io.john6.test.webviewcachetest.WebViewCache

class JsWithCacheWebFragment: BaseWebFragment() {

    private var currentWebView: WebView? = null

    override fun getWebView(): WebView {
        val desireUrl = "file:///android_asset/demo_js_interface.html"

        return WebViewCache.acquireOrCreate(requireContext(), desireUrl.toUri()).apply {
            this@JsWithCacheWebFragment.currentWebView = this
            WebViewCache.setupWebView(this)
            webViewClient = object: WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    printMsg.add("${SystemClock.uptimeMillis() - startTime}ms pageFinished, progress:${view?.progress}")
                    if(view?.progress == 100){
                        startCollectData(view)
                    }
                }
            }
            if (this.url.isNullOrBlank()) {
                loadUrl(desireUrl)
            } else {
                startCollectData(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentWebView?.also { WebViewCache.releaseSafe(it) }
        currentWebView = null
    }
}