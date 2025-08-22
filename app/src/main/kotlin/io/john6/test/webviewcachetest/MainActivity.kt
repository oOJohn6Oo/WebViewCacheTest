@file:OptIn(ExperimentalMaterial3Api::class)

package io.john6.test.webviewcachetest

import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import io.john6.test.webviewcachetest.dialog.WebBottomSheetDialogFragment
import io.john6.test.webviewcachetest.dialog.WebType

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.getMainLooper().queue.addIdleHandler(WebViewCache.preloadIdleHandler)
        }
        setContent {
            MaterialTheme {

                Box {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if(isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background)
                            .padding(vertical = 12.dp)
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(
                            modifier = Modifier.defaultMinSize(minWidth = 240.dp),
                            onClick = this@MainActivity::showJSWebDialog,
                            content = {
                                Text("JS")
                            },
                        )
                        TextButton(
                            modifier = Modifier.defaultMinSize(minWidth = 240.dp),
                            onClick = this@MainActivity::showJSWithCacheWebDialog,
                            content = {
                                Text("JSWithCache")
                            },
                        )
                        TextButton(
                            modifier = Modifier.defaultMinSize(minWidth = 240.dp),
                            onClick = this@MainActivity::showWebMessageWithCacheWebDialog,
                            content = {
                                Text("WebMessageWithCache")
                            },
                        )
                    }
                }
            }
        }
    }

    private fun showJSWebDialog() {
        doShowWebDialog(WebType.Js)
    }

    private fun showJSWithCacheWebDialog() {
        doShowWebDialog(WebType.JsWithCache)
    }

    private fun showWebMessageWithCacheWebDialog() {
        doShowWebDialog(WebType.MessageWithCache)
    }

    private fun doShowWebDialog(webType: WebType) {
        WebBottomSheetDialogFragment().apply {
            arguments = Bundle().apply {
                putString("webType", webType.type)
            }
            show(this@MainActivity.supportFragmentManager, "WebBottomSheetDialogFragment")
        }
    }
}