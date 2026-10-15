package com.example.nvb_dilan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nvb_dilan.Home.myProjek.dashboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpalshScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh_screen)

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, dashboard::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_spalsh_screen)

        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@SpalshScreenActivity, BaseActivity::class.java))
            finish()
        }
    }
}