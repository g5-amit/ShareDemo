package com.example.sharedemo

import android.app.Application
import android.content.MutableContextWrapper
import com.example.sharedemo.webview.SingletonWebViewPool

class ShareApp : Application() {
    lateinit var  webPool: SingletonWebViewPool
    override fun onCreate() {
        super.onCreate()
        webPool = SingletonWebViewPool.getInstance(MutableContextWrapper(this))
    }


}