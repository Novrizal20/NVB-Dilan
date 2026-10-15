package com.example.nvb_dilan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.databinding.ActivityMainBinding
import com.example.nvb_dilan.Home.myProjek.Profil
import com.example.nvb_dilan.Home.myProjek.Projek
import com.example.nvb_dilan.Home.myProjek.WebViewActivity
import com.example.nvb_dilan.Home.myProjek.dashboard
import com.example.nvb_dilan.Home.myProjek.login
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, dashboard::class.java))
            finish()
            return
        }
        binding.btnOpen.setOnClickListener {
            startActivity(Intent(this, Projek::class.java))
        }

        binding.btnMember.setOnClickListener {
            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val isLogin = sharedPref.getBoolean("isLogin", false)

            if (isLogin) {
                startActivity(Intent(this, dashboard::class.java))
            } else {
                startActivity(Intent(this, login::class.java))
            }
        }
        binding.btnWebView.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }
        binding.btnProfil.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }
    }
}