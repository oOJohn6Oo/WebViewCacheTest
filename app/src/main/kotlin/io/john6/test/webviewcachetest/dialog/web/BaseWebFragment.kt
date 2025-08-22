package io.john6.test.webviewcachetest.dialog.web

import android.annotation.SuppressLint
import android.os.*
import android.view.*
import android.webkit.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseWebFragment: Fragment() {

    protected val printMsg = mutableListOf("======")
    protected var startTime = 0L
    private set

    private var webMessagePort: WebMessagePort? = null

    protected val dataFlow = flow {
        repeat(55){
            emit("This is a test data flow simulate response from SSE API".take(it + 1))
            delay(20)
        }
    }.flowOn(Dispatchers.IO)

    fun wrapInScrollView(webView: WebView): NestedScrollView {
        val scrollView = NestedScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(webView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            )
        }
        return scrollView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        startTime = SystemClock.uptimeMillis()
        printMsg.add("0ms init")
        return wrapInScrollView(getWebView())
    }

    @SuppressLint("WebViewApiAvailability")
    protected fun startCollectData(webView: WebView, usingJS:Boolean = true){
        viewLifecycleOwner.lifecycleScope.launch {
            if(!usingJS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                printMsg.add("${SystemClock.uptimeMillis() - startTime}ms create web message port")
                // 创建通道
                val channels = webView.createWebMessageChannel()
                webMessagePort = channels[0]
            }
            printMsg.add("${SystemClock.uptimeMillis() - startTime}ms start collect data")
            dataFlow.collect {
                val content = "${printMsg.joinToString("\\n")}\\n======\\n\\nReceived:\\n$it"
                if(!usingJS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    webMessagePort?.postMessage(WebMessage(content))
                }else{
                    webView.evaluateJavascript("updateFromNative('$content')", null)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webMessagePort?.close()
        }
    }

    abstract fun getWebView(): WebView
}