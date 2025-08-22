package io.john6.test.webviewcachetest

import android.app.Application
import android.content.Context


lateinit var appContext: Context

class MyApp: Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this.applicationContext
    }
}