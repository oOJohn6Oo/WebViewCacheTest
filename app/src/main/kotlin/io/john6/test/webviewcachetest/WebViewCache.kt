package io.john6.test.webviewcachetest

import android.annotation.SuppressLint
import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Color
import android.net.Uri
import android.os.MessageQueue
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.MainThread
import androidx.core.net.toUri
import androidx.core.util.Pools

private const val TAG = "WebViewCache"

/**
 * [android.webkit.WebView] Cache pool
 **/
object WebViewCache {
    private const val MAX_POOL_SIZE = 8

    private val mPools: MutableMap<String, Pools.Pool<WebView>> = mutableMapOf()
    private val emptyWebViewClient = object : WebViewClient() {}

    val preloadIdleHandler = object : MessageQueue.IdleHandler {
        override fun queueIdle(): Boolean {
            if (mPools.isNotEmpty()) return false
            val desireUrl = "file:///android_asset/demo_js_interface.html"
            preloadWebView(desireUrl)
            return false
        }
    }

    fun acquireOrCreate(context: Context, url: Uri? = null): WebView {
        val hostAndPath = (url?.host ?: "") + (url?.path ?: "")
        if (!mPools.contains(hostAndPath)) {
            mPools[hostAndPath] = Pools.SynchronizedPool(MAX_POOL_SIZE)
        }
        var webView = mPools[hostAndPath]?.acquire()

        if (webView == null) {
            val wrapper = MutableContextWrapper(context)
            webView = WebView(wrapper)
        } else {
            (webView.context as? MutableContextWrapper)?.baseContext = context
        }
        return webView
    }

    @MainThread
    fun releaseSafe(webView: WebView) = webView.apply {
        Log.d(TAG, "releaseWebView $webView")
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.stopLoading()
        (webView.context as? MutableContextWrapper)?.baseContext = appContext
        webView.webViewClient = emptyWebViewClient
        val uri = webView.url?.toUri()
        val hostAndPath = (uri?.host ?: "") + (uri?.path ?: "")
        mPools[hostAndPath]?.release(webView)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView(web: WebView) {
        web.setBackgroundColor(Color.TRANSPARENT)
        web.settings.apply {
            javaScriptEnabled = true
        }
    }

    fun preloadWebView(desireUrl: String) {
        acquireOrCreate(appContext, desireUrl.toUri()).apply {
            setupWebView(this)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.let { releaseSafe(it) }
                }
            }
            loadUrl(desireUrl)
        }
    }

    @MainThread
    fun WebView.safeDestroy() {
        (parent as? ViewGroup)?.removeView(this)
        stopLoading()
        clearHistory()
        clearCache(true)
        destroy()
    }

    /**
     * You may never need to do this
     */
    @Suppress("unused")
    @MainThread
    fun clear() {
        mPools.keys.forEach {
            while (true) {
                mPools[it]?.acquire()?.safeDestroy() ?: break
            }
        }
        mPools.clear()
    }

}