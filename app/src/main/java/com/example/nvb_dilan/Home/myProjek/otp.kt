package com.example.nvb_dilan.Home.myProjek

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityOtpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class otp : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("EXTRA_NAME") ?: ""
        val phone = intent.getStringExtra("EXTRA_PHONE") ?: ""
        val email = intent.getStringExtra("EXTRA_EMAIL") ?: ""
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: ""
        val password = intent.getStringExtra("EXTRA_PASSWORD") ?: ""

        binding.btnVerifyOtp.setOnClickListener {
            val inputOtp = binding.etOtpInput.text.toString().trim()

            // Rule Soal 2: OTP harus sama dengan No. Handphone
            if (inputOtp.isNotEmpty() && inputOtp == phone) {
                // Simpan ke SharedPreferences
                val sharedPref = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("NAME", name)
                    putString("PHONE", phone)
                    putString("EMAIL", email)
                    putString("USERNAME", username)
                    putString("PASSWORD", password)
                    apply()
                }

                val intent = Intent(this, login::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            } else {
                // Tampilkan Error dengan MaterialAlertDialog
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verifikasi Gagal")
                    .setMessage("Kode OTP tidak sesuai dengan nomor handphone yang didaftarkan.")
                    .setPositiveButton("Coba Lagi") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }
}