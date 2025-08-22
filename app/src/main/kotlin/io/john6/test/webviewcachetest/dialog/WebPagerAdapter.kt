package io.john6.test.webviewcachetest.dialog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.john6.test.webviewcachetest.dialog.web.JsWebFragment
import io.john6.test.webviewcachetest.dialog.web.JsWithCacheWebFragment


class WebPagerAdapter(var webType: WebType, fm: FragmentManager, lifeCycle: Lifecycle) :
    FragmentStateAdapter(fm, lifeCycle) {
    override fun createFragment(position: Int) = when (webType) {
        WebType.Js -> {
            JsWebFragment()
        }

        WebType.JsWithCache -> {
            JsWithCacheWebFragment()
        }

        else -> {
            JsWithCacheWebFragment()
        }
    }.apply {
        Log.d("lq", "createFragment ${position + 1}")
        arguments = Bundle().apply {
            putInt("pos", position)
        }
    }

    override fun getItemCount() = 12
}