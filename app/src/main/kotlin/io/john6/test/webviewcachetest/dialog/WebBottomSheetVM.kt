package io.john6.test.webviewcachetest.dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class WebBottomSheetVM(savedStateHandle: SavedStateHandle): ViewModel() {
    val webType: WebType = (savedStateHandle["webType"] ?: "").map2WebType()
}