package io.john6.test.webviewcachetest

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.widget.FrameLayout

class MaxAvailableHeightFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode == MeasureSpec.UNSPECIFIED) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }else{
            val available = MeasureSpec.getSize(heightMeasureSpec)
            val forcedHeightSpec = MeasureSpec.makeMeasureSpec(available, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, forcedHeightSpec)
        }

    }
}
