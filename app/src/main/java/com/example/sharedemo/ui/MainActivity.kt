package com.example.sharedemo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedemo.databinding.ActivityMainBinding
import com.example.sharedemo.webview.HeadlessBrowserWebViewActivity
import com.example.sharedemo.webview.PreLaunchWebViewActivity
import com.example.sharedemo.webview.ShareWebViewActivity

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.btnShare.setOnClickListener {
            val share = ShareBottomSheetDialog("Use Lists App to manage yours day to day work!")
            share.show(supportFragmentManager, "Share Across Apps")
        }

        val intent = Intent(this, ShareWebViewActivity::class.java)
        _binding.btnWebView.setOnClickListener{
            startActivity(intent)
        }

        val intent2 = Intent(this, HeadlessBrowserWebViewActivity::class.java)
        _binding.btnHeadlessWebview.setOnClickListener{
            startActivity(intent2)
        }

        val intent3 = Intent(this, PreLaunchWebViewActivity::class.java)
        _binding.btnPrelaunchWebview.setOnClickListener{
            startActivity(intent3)
        }
    }
}

