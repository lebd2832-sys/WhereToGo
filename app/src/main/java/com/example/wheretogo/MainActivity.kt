package com.example.wheretogo

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import kotlin.concurrent.thread

/** 화면(HTML)이 한국관광공사 TourAPI를 부를 때 쓰는 다리. 인증키는 앱 안(BuildConfig)에만 있고 화면 코드에는 없다. */
class Bridge(private val web: WebView, private val rawKey: String) {
    private val key: String = if (rawKey.contains("%")) URLDecoder.decode(rawKey, "UTF-8") else rawKey

    @JavascriptInterface fun hasKey(): Boolean = key.isNotEmpty()

    @JavascriptInterface
    fun tour(id: String, endpoint: String, paramsJson: String) {
        thread {
            var ok = false
            var text: String
            try {
                if (!endpoint.matches(Regex("[A-Za-z0-9]+"))) throw IllegalArgumentException("bad endpoint")
                val p = JSONObject(paramsJson)
                val sb = StringBuilder("https://apis.data.go.kr/B551011/KorService2/")
                    .append(endpoint)
                    .append("?serviceKey=").append(Uri.encode(key))
                    .append("&MobileOS=AND&MobileApp=WhereToGo&_type=json")
                for (k in p.keys()) sb.append("&").append(Uri.encode(k)).append("=").append(Uri.encode(p.get(k).toString()))
                val c = URL(sb.toString()).openConnection() as HttpURLConnection
                c.connectTimeout = 10000; c.readTimeout = 12000
                text = c.inputStream.bufferedReader().use { it.readText() }
                ok = true
            } catch (e: Exception) { text = e.message ?: "error" }
            val js = "window.__tourCb(" + JSONObject.quote(id) + "," + ok + "," + JSONObject.quote(text) + ")"
            web.post { web.evaluateJavascript(js, null) }
        }
    }
}

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
        web.addJavascriptInterface(Bridge(web, BuildConfig.TOUR_KEY), "Native")
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
