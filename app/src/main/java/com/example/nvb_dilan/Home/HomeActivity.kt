package com.example.nvb_dilan.Home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.BaseActivity
import com.example.nvb_dilan.Home.myProjek.Profil
import com.example.nvb_dilan.Home.myProjek.Projek
import com.example.nvb_dilan.Home.myProjek.WebViewActivity
import com.example.nvb_dilan.Home.myProjek.login
import com.example.nvb_dilan.databinding.ActivityHomeBinding

/** Halaman awal aplikasi: Activity biasa (tanpa fragment dan tanpa toolbar). */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpen.setOnClickListener {
            startActivity(Intent(this, Projek::class.java))
        }

        binding.btnMember.setOnClickListener {
            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val isLogin = sharedPref.getBoolean("isLogin", false)

            if (isLogin) {
                startActivity(Intent(this, BaseActivity::class.java))
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
