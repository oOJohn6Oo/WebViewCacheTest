package io.john6.test.webviewcachetest.dialog

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import io.john6.test.webviewcachetest.databinding.FragmentWebBinding


class WebBottomSheetDialogFragment: BottomSheetDialogFragment() {

    private var _mBinding: FragmentWebBinding? = null
    val mBinding: FragmentWebBinding
        get() = _mBinding!!

    val mVM by viewModels<WebBottomSheetVM>()

    private lateinit var mAdapter: WebPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mBinding = FragmentWebBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupBottomSheetBehavior()
    }

    private fun setupViewPager() {
        mAdapter = WebPagerAdapter(mVM.webType, childFragmentManager, lifecycle)
        mBinding.viewpagerFgWeb.adapter = mAdapter
        mBinding.viewpagerFgWeb.offscreenPageLimit = 1

        (mBinding.viewpagerFgWeb.getChildAt(0) as RecyclerView).apply {
            // Only enable this line result in keeping 5 fragment
            layoutManager?.isItemPrefetchEnabled = false
            // Only enable this line result in keeping 4 fragment
            setItemViewCacheSize(0)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        mBinding.viewpagerFgWeb.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                // Seems like there's no problem with this ViewPager
//                if (state == ViewPager2.SCROLL_STATE_IDLE) {
//                    refreshScrollingChild()
//                }
            }
        })


        TabLayoutMediator(mBinding.tabFgWeb, mBinding.viewpagerFgWeb) { tab, position ->
            tab.text = "${position + 1}"
        }.attach()
    }

    @Suppress("unused")
    private fun refreshScrollingChild(){
        val decorView = dialog?.window?.decorView ?: return
        val coordinatorLayout = decorView.findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
        val bottomSheet = coordinatorLayout.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.onLayoutChild(
            coordinatorLayout,
            bottomSheet,
            coordinatorLayout.layoutDirection
        )
    }

    private fun setupBottomSheetBehavior() {

        (dialog as BottomSheetDialog).behavior.run {
            isGestureInsetBottomIgnored = false
            halfExpandedRatio = 0.5f
            isFitToContents = false
            isShouldRemoveExpandedCorners = false
            isHideable = false
            isDraggable = true
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

    }

}