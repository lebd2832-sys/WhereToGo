package com.example.wheretogo

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {
    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        web = WebView(this)
        setContentView(web)
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true            // 설정·남은 기회·내가 추가한 곳 저장(localStorage)
            mediaPlaybackRequiresUserGesture = false
        }
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean {
                // 지도 열기 등 바깥 링크는 기본 브라우저/지도 앱으로
                if (r.url.scheme == "http" || r.url.scheme == "https") {
                    startActivity(Intent(Intent.ACTION_VIEW, r.url)); return true
                }
                return false
            }
        }
        web.loadUrl("file:///android_asset/index.html")
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 앱 안 화면 뒤로가기(‹)를 먼저 시도하고, 첫 화면이면 종료
                web.evaluateJavascript("(function(){var b=document.querySelector('.screen.on .icon-btn');var s=document.querySelector('.screen.on');if(s&&s.id!=='s-map'&&b){b.click();return 1}return 0})()") {
                    if (it != "1") { isEnabled = false; onBackPressedDispatcher.onBackPressed() }
                }
            }
        })
    }
}
