package io.john6.test.webviewcachetest.dialog.web

import android.os.SystemClock
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import io.john6.test.webviewcachetest.WebViewCache

class ChannelWithCacheWebFragment: BaseWebFragment() {

    private var currentWebView: WebView? = null

    override fun getWebView(): WebView {
        val desireUrl = "file:///android_asset/demo_web_message.html"

        return WebViewCache.acquireOrCreate(requireContext(), desireUrl.toUri()).apply {
            this@ChannelWithCacheWebFragment.currentWebView = this

            WebViewCache.setupWebView(this)

            webViewClient = object: WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    printMsg.add("${SystemClock.uptimeMillis() - startTime}ms pageFinished, progress:${view?.progress}")
                    if(view?.progress == 100){
                        startCollectData(view, false)
                    }
                }
            }

            if (this.url.isNullOrBlank()) {
                loadUrl(desireUrl)
            } else {
                startCollectData(this, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentWebView?.also { WebViewCache.releaseSafe(it) }
        currentWebView = null
    }
}