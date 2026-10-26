package com.example.nvb_dilan.Home.myProjek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.BaseActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityLoginBinding
import com.example.nvb_dilan.setupToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Pengaman: kalau sudah login, langsung ke area Member+ Hub
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, BaseActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_login))

        binding.btnSubmit.setOnClickListener {
            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Data akun hasil registrasi (disimpan oleh otp.kt)
            val savedUser = sharedPref.getString("reg_username", null)
            val savedPass = sharedPref.getString("reg_password", null)

            // Rule 1: username sama dengan password -> boleh login
            val isRule1Valid = username.isNotEmpty() && username == password

            // Rule 2: username dan password sama dengan data hasil registrasi -> boleh login
            val isRule2Valid = !savedUser.isNullOrEmpty() &&
                    !savedPass.isNullOrEmpty() &&
                    username == savedUser &&
                    password == savedPass

            if (isRule1Valid || isRule2Valid) {
                // Simpan status login dan username
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()

                // Masuk ke area Member+ Hub (DashboardFragment tampil pertama).
                // Back stack dikosongkan agar tombol Back langsung keluar aplikasi.
                val intent = Intent(this, BaseActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Login Gagal")
                    .setMessage("Username atau Password tidak sah! Sila cuba lagi.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        // Ke halaman pendaftaran (register)
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }
    }
}
