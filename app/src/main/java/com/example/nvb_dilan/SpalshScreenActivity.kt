package com.example.nvb_dilan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nvb_dilan.Home.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpalshScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sudah login -> langsung ke area Member+ Hub
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, BaseActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_spalsh_screen)

        // Belum login -> halaman awal (HomeActivity) setelah 2 detik
        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@SpalshScreenActivity, HomeActivity::class.java))
            finish()
        }
    }
}
