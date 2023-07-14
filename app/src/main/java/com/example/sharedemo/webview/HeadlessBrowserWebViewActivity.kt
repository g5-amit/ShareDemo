package com.example.sharedemo.webview

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.sharedemo.R

class HeadlessBrowserWebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var bridgeBtn: AppCompatButton


    companion object {
        const val APP_SCHEME = "my-app-scheme:"
        const val webURL =
            "https://amit-gupta-react-perf.vercel.app"//"https://my-app-g5-amit.vercel.app"//"https://amit-gupta-react-perf.vercel.app" // "https://www.wikipedia.org"
        const val IS_JSON_MESSAGE = true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)


        Log.d("amit123", "webview load start time = " + System.currentTimeMillis())
        webView = findViewById(R.id.webView)
        bridgeBtn = findViewById(R.id.bridge_button)

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.webViewClient = WebViewClient()

        // if you want to enable zoom feature
        webView.settings.setSupportZoom(true)

        //webview settings for cache
        webView.settings.allowFileAccess = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        /**
         * call Android Interface from webpage
         * this will enable the javascript settings, it can also allow xss vulnerabilities
         */
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")

        /**
         * Intercept Web page Url call through interface
         */
//        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsPrompt(
                view: WebView,
                origin: String,
                message: String,
                frame: String,
                result: JsPromptResult
            ): Boolean {
//                if (message.startsWith("window.Android.showToast")) {
                    val messageBody = message.substring(message.indexOf("'") + 1, message.lastIndexOf("'"))
                    Toast.makeText(this@HeadlessBrowserWebViewActivity, messageBody, Toast.LENGTH_SHORT).show()
                    result.confirm()
                    return true
//                }
//                return false
            }
        }

        // this will load the url of the website
        webView.loadUrl(webURL)
        bridgeBtn.setOnClickListener {
            transferDataToJs(fetchScript(), webView)
        }
    }

    private fun transferDataToJs(script: String, webView: WebView) {
        webView.evaluateJavascript(script) {
                s -> receiveDataFromJs(s)
        }
    }

    private fun receiveDataFromJs(data: String?) {
        Toast.makeText(applicationContext, "data =$data", Toast.LENGTH_SHORT).show()
    }

    private fun fetchScript(): String {
        val functionName: String  //"callHeadlessBrowserMessage"
        val functionArg1: String
        val functionArg2: String
        if (IS_JSON_MESSAGE) {
            functionName = "callJsonMessage"
            functionArg2 = ""
            functionArg1 = WebViewUtils.mapObjectToJsonString(WebMainPojo())
        } else {
            functionName = "callJSMessage"
            functionArg2 = "with 2nd argument"
            functionArg1 = "Android To JS Function call"
        }
        //            return "javascript:window.Android.showToast('Hello from Android!');"
        return WebViewUtils.formatScript(
            functionName,
            functionArg1,
            functionArg2
        )
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

}