package com.example.sharedemo.webview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.sharedemo.R
import java.net.URLDecoder


class ShareWebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var bridgeBtn: AppCompatButton


    companion object{
        const val APP_SCHEME = "my-app-scheme:"
        const val webURL = "https://amit-gupta-react-perf.vercel.app"//"https://my-app-g5-amit.vercel.app"//"https://amit-gupta-react-perf.vercel.app" // "https://www.wikipedia.org"
        const val IS_JSON_MESSAGE = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)


        Log.d("amit123", "webview load start time = " +System.currentTimeMillis())
        webView = findViewById(R.id.webView)
        bridgeBtn= findViewById(R.id.bridge_button)

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.webViewClient = MyWebViewClient()

        // if you want to enable zoom feature
        webView.settings.setSupportZoom(true)

        /**
        * call Android Interface from webpage
        * this will enable the javascript settings, it can also allow xss vulnerabilities
        */
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        //val jsExample = "<input type=\"button\" value=\"Say hello\" onClick=\"showAndroidToast('Hello Android!')\" /><script type=\"text/javascript\">function showAndroidToast(toast) {Android.showToast(toast);}</script>"

        /**
         * Intercept Web page Url call through interface
         */
        webView.webViewClient = MyWebViewClient()

        //webview settings for cache
        webView.settings.allowFileAccess = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        // this will load the url of the website
        webView.loadUrl(webURL)
//        val headers: MutableMap<String, String> = HashMap()
//        headers["If-None-Match"] = "abcd1234" // get it from shared preferences
//        webView.loadUrl(webURL, headers)

        bridgeBtn.setOnClickListener{
            val functionName = "callJSMessage"
            val functionArg1: String
            val functionArg2: String
            if (IS_JSON_MESSAGE) {
                functionArg2 = ""
                functionArg1 = WebViewUtils.mapObjectToJsonString(WebMainPojo())
            } else {
                functionArg2 = "with 2nd argument"
                functionArg1 = "Android To JS Function call"
            }
            val script = WebViewUtils.formatScript(
                functionName,
                functionArg1,
                functionArg2
            )

            transferDataToJs(script, webView)
        }
    }

    private fun transferDataToJs(script: String, webView: WebView) {
        webView.evaluateJavascript(script) {
                jsonString -> receiveDataFromJs(jsonString)
        }

//        webView.evaluateJavascript("callJSMessage('Android To JS Function call Success')")
//        {
//                s -> returnDataFromJs(s)
//        }
    }

    private fun receiveDataFromJs(data: String?) {
        Toast.makeText(applicationContext, "data =$data", Toast.LENGTH_SHORT).show()
    }

    // if you press Back button this code will work
    override fun onBackPressed() {
        // if your webview can go back it will go back
        if (webView.canGoBack())
            webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }


    private class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.d("amit123", url?:"")
            if (Uri.parse(url)?.host?.startsWith(webURL) == true) {
                /**
                 * This is my web site, so do not override; let my WebView load the page
                 */
                return false
            }else if (url?.startsWith(APP_SCHEME) == true) {
                /**
                 * Url/uri/scheme I want to handle myself natively instead of my webview
                 */
                val urlData = URLDecoder.decode(Uri.parse(url).host?.substring(APP_SCHEME.length), "UTF-8")
//                respondToData(urlData)
                return true
            }
            /**
             * Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
             */
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                view?.context?.startActivity(this)
            }
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            Log.d("amit123", request?.url?.host?:"")
            if (request?.url?.host?.startsWith(webURL) == true) {
                /**
                 * This is my web site, so do not override; let my WebView load the page
                 */
                return false
            }else if (request?.url?.host?.startsWith(APP_SCHEME) == true) {
                /**
                 * Url/uri/scheme I want to handle myself natively instead of my webview
                 */
                val urlData = URLDecoder.decode(request.url?.host?.substring(APP_SCHEME.length), "UTF-8")
//                respondToData(urlData)
                return true
            }
            /**
             * Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
             */
            Intent(Intent.ACTION_VIEW, request?.url).apply {
                view?.context?.startActivity(this)
            }
            return true
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            // Check if the request contains the ETag header
            // Check if the request contains the ETag header
            if (request!!.requestHeaders.containsKey("If-None-Match")) {
                val eTag = request.requestHeaders["If-None-Match"]

                // Store the ETag value in a shared preference or any other storage mechanism
                storeETagValue(eTag)
            }

            return super.shouldInterceptRequest(view, request)
        }
        private fun storeETagValue(eTag: String?) {
            // Store the ETag value into SharedPreferences
            // ...
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            Log.d("amit123", "webview load end time = " +System.currentTimeMillis())
            Log.d("amit123", "js main frame load start time=" + System.currentTimeMillis())
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("amit123", "js main frame load end time=" + System.currentTimeMillis())
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }
    }

}