package io.john6.test.webviewcachetest.dialog.web

import android.os.SystemClock
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import io.john6.test.webviewcachetest.WebViewCache
import kotlinx.coroutines.launch

class JsWebFragment: BaseWebFragment() {

    override fun getWebView(): WebView {
        val desireUrl = "file:///android_asset/demo_js_interface.html"
        return WebView(requireContext()).apply {
            printMsg.add("${SystemClock.uptimeMillis() - startTime}ms WebView created")
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            WebViewCache.setupWebView(this)

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    printMsg.add("${SystemClock.uptimeMillis() - startTime}ms pageFinished, progress:${view?.progress}")
                    if(view?.progress == 100){
                        startCollectData(view)
                    }
                }
            }
            loadUrl(desireUrl)
        }
    }
}