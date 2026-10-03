package com.example.nvb_dilan.myProjek

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.setupToolbar
import com.example.nvb_dilan.databinding.ActivityLoginBinding

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isRegister = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_login))
        renderMode()

        binding.tvToggle.setOnClickListener {
            isRegister = !isRegister
            renderMode()
        }

        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                val pesan = if (isRegister) "Pendaftaran berhasil" else "Berhasil masuk"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, dashboard::class.java))
                finish() // tombol back dari dashboard kembali ke halaman awal
            }
        }
    }
    private fun renderMode() {
        val judul = if (isRegister) "Daftar" else "Masuk"

        binding.tilNama.visibility = if (isRegister) android.view.View.VISIBLE else android.view.View.GONE
        binding.tilKonfirmasi.visibility = if (isRegister) android.view.View.VISIBLE else android.view.View.GONE

        binding.tvFormTitle.text = judul
        binding.tvFormSub.text = if (isRegister)
            "Buat akun untuk mulai kumpulkan poin"
        else
            "Masuk untuk melihat poin dan reward kamu"
        binding.btnSubmit.text = judul
        binding.tvToggle.text = if (isRegister)
            "Sudah punya akun? Masuk"
        else
            "Belum punya akun? Daftar"

        supportActionBar?.title = getString(
            if (isRegister) R.string.title_register else R.string.title_login
        )
        binding.tilNama.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilKonfirmasi.error = null
    }

    private fun validateForm(): Boolean {
        val nama = binding.etNama.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val konfirmasi = binding.etKonfirmasi.text.toString()
        var valid = true

        binding.tilNama.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilKonfirmasi.error = null

        if (isRegister && nama.isEmpty()) {
            binding.tilNama.error = "Nama wajib diisi"
            valid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            valid = false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password minimal 6 karakter"
            valid = false
        }
        if (isRegister && konfirmasi != password) {
            binding.tilKonfirmasi.error = "Konfirmasi password tidak sama"
            valid = false
        }
        return valid
    }
}