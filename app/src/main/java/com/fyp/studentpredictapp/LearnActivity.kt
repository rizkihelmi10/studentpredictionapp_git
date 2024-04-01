package com.fyp.studentpredictapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class LearnActivity : AppCompatActivity() {
    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn2)
        webView = findViewById(R.id.webView);

        // Load a web page
        webView.loadUrl("https://elearning.usm.my/sidang2324/login/index.php");
    }
}