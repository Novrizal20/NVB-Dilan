package com.example.nvb_dilan.Home.myProjek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
                // Simpan ke SharedPreferences (nama "user_pref" sama dengan yang dibaca login.kt)
                val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("reg_name", name)
                    putString("reg_phone", phone)
                    putString("reg_email", email)
                    putString("reg_username", username)
                    putString("reg_password", password)
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
