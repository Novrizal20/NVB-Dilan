package com.example.nvb_dilan.myProjek

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityWebViewBinding
import com.example.nvb_dilan.setupToolbar
import androidx.activity.OnBackPressedCallback

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_web))

        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.loadUrl("https://project-kel9.vercel.app/")

        // Toolbar sembunyi saat scroll ke bawah, muncul saat scroll ke atas
        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.layoutToolbar.appBar.setExpanded(false, true)
            } else if (scrollY < oldScrollY) {
                binding.layoutToolbar.appBar.setExpanded(true, true)
            }
        }
    }
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack() // Kembali ke halaman web sebelumnya
        } else {
            super.onBackPressed() // Keluar activity sebelumnya
        }
    }
}