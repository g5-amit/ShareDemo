package com.example.sharedemo.webview

import android.content.Context
import android.content.MutableContextWrapper
import android.webkit.WebView


class SingletonWebViewPool private constructor(appCtx: Context?) : WebViewPool {


    private var mCachedInstance: WebView? = null

    @Volatile
    private var mBorrower = 0
    private var mSignature = 0
    private var mAppCtx: Context? = null

    init {
        mAppCtx = appCtx
        mCachedInstance = WebView(appCtx!!)
        // We will match this every time, we flip to app context to ensure a different web view is
        // not handed over to us.
        mSignature = mCachedInstance.hashCode()
    }

    companion object{
        @Volatile
        private var instance: SingletonWebViewPool? = null

        fun getInstance(appCtx: Context?): SingletonWebViewPool {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = SingletonWebViewPool(appCtx)
                    }
                }
            }
            return instance!!
        }
    }

    override fun obtain(activity: Context): WebView {
        return if (mCachedInstance != null) {
            val ctx = mCachedInstance!!.context
            if (ctx is MutableContextWrapper) {
                ctx.baseContext = MutableContextWrapper(activity)
            } else {
                // We should not reach here!
                throw IllegalStateException("Cached web view stored without a mutable context wrapper.")
            }
            val temp: WebView = mCachedInstance as WebView
            mCachedInstance = null
            mBorrower = activity.hashCode()
            temp
        } else {
            throw IllegalStateException("Pool not having a cached web view instance when obtain() was called.")
        }
    }

    override fun release(webView: WebView, borrower: Context): Boolean {
        // Validate the last borrower.
        if (borrower.hashCode() != mBorrower) {
            return false
        }
        val ctx = webView.context
        if (ctx is MutableContextWrapper) {
            ctx.baseContext = mAppCtx
        } else {
            throw IllegalStateException("Cached web view stored without a mutable context wrapper.")
        }

        // match the signature.
        check(mSignature == webView.hashCode()) { "A different web view is released other than what we have given out." }
        mCachedInstance = webView
        mBorrower = 0
        return true
    }
}