package com.example.nvb_dilan.myProjek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityLoginBinding
import com.example.nvb_dilan.setupToolbar

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Pengaman: kalau sudah login, langsung ke dashboard Member+ Hub
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, dashboard::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_login))

        binding.btnSubmit.setOnClickListener {
            val username = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isNotEmpty() && username == password) {
                // Simpan status login dan username
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()

                // Masuk ke dashboard Member+ Hub
                startActivity(Intent(this, dashboard::class.java))
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setMessage("Silahkan coba lagi")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}