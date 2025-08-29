# Demonstrate how to render WebView content instantly

## Key logic

* Using host + path as Key, and WebView as Value in a map to store and reuse the WebView
  <details>
   <summary>WebViewCache.kt</summary>
   
   ``` kt
   object WebViewCache {
    private const val MAX_POOL_SIZE = 8

    private val mPools: MutableMap<String, Pools.Pool<WebView>> = mutableMapOf()
    private val emptyWebViewClient = object : WebViewClient() {}
  
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
  }
   ```
  </details>
* Prevent to reload the url when WebView.url isNotBlank
  <details>
   <summary>Demo logic</summary>

   ``` kt
   WebViewCache.acquireOrCreate(requireContext(), desireUrl.toUri()).apply {
    this@JsWithCacheWebFragment.currentWebView = this
    WebViewCache.setupWebView(this)
    webViewClient = object: WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
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
   ```
  </details>
* Using idleHandler to pre-load WebView
  <details>
   <summary>Pre-load Code</summary>

   ``` kt
   Looper.getMainLooper().queue.addIdleHandler(WebViewCache.preloadIdleHandler)
   ```

   ``` kt
   val preloadIdleHandler = object : MessageQueue.IdleHandler {
        override fun queueIdle(): Boolean {
            if (mPools.isNotEmpty()) return false
            val desireUrl = "file:///android_asset/demo_js_interface.html"
            preloadWebView(desireUrl)
            return false
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
   ```
  </details>

Move to [juejin.cn](https://juejin.cn/post/7543556350745313306) to see the whole blog.

## Performance test
> Release + R8 + Local HTML + My Device

 | Type/Duration | Finish Create(avg) | Start Render(avg) |
 | :---: | :---: | :---: |
 | No Cache first Time| 104ms | 230ms |
 | No Cache later Time| 3ms | 64ms |
 | Cached | 0ms | 2ms |



## Result

![no cache vs cached WebView render](https://i.imgur.com/ZEivLwz.png)
