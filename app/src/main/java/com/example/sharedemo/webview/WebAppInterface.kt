package com.example.sharedemo.webview

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) : String {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        return "Hey JS , catch this data from Android Native in return"
    }
}