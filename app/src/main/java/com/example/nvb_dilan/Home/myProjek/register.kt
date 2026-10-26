package com.example.nvb_dilan.Home.myProjek

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityRegisterBinding

class register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim data registrasi ke halaman OTP
            val intent = Intent(this, otp::class.java).apply {
                putExtra("EXTRA_NAME", name)
                putExtra("EXTRA_PHONE", phone)
                putExtra("EXTRA_EMAIL", email)
                putExtra("EXTRA_USERNAME", username)
                putExtra("EXTRA_PASSWORD", password)
            }
            startActivity(intent)
        }
    }
}