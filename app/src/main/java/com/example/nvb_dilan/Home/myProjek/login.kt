package com.example.nvb_dilan.Home.myProjek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityLoginBinding
import com.example.nvb_dilan.setupToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Capaian SharedPreferences
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Kawalan keselamatan: Jika pengguna sudah login, terus buka dashboard
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, dashboard::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_login))

        binding.btnSubmit.setOnClickListener {
            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Ambil data akaun yang didaftarkan daripada SharedPreferences
            val savedUser = sharedPref.getString("username", null)
            val savedPass = sharedPref.getString("password", null)

            // Rule 1: Jika username = password -> Boleh Login
            val isRule1Valid = username.isNotEmpty() && username == password

            // Rule 2: Jika username = username di SharedPreferences dan password = password di SharedPreferences -> Boleh Login
            val isRule2Valid = !savedUser.isNullOrEmpty() &&
                    !savedPass.isNullOrEmpty() &&
                    username == savedUser &&
                    password == savedPass

            // Semak jika mana-mana syarat (Rule 1 atau Rule 2) dipenuhi
            if (isRule1Valid || isRule2Valid) {
                // Simpan status login ke SharedPreferences
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()

                // Berpindah ke halaman dashboard
                startActivity(Intent(this, dashboard::class.java))
                finish()
            } else {
                // Papar paparan ralat menggunakan MaterialAlertDialogBuilder
                MaterialAlertDialogBuilder(this)
                    .setTitle("Login Gagal")
                    .setMessage("Username atau Password tidak sah! Sila cuba lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        // Navigasi ke halaman pendaftaran (register)
        binding.btnRegister?.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }
    }
}