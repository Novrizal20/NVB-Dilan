package com.example.nvb_dilan.Home.myProjek

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nvb_dilan.databinding.ActivityOtpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class otp : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    private var phone = ""
    private var kodeTerkirim: String? = null

    private val izinSms = registerForActivityResult(ActivityResultContracts.RequestPermission()) { diberi ->
        if (diberi) {
            kirimKodeSms()
        } else {
            val kode = generateDanTampilkanKode()
            MaterialAlertDialogBuilder(this)
                .setTitle("Izin SMS Ditolak")
                .setMessage("Aplikasi tidak diizinkan mengirim SMS. Kode OTP untuk pengujian di layar: $kode")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("EXTRA_NAME") ?: ""
        phone = intent.getStringExtra("EXTRA_PHONE") ?: ""
        val email = intent.getStringExtra("EXTRA_EMAIL") ?: ""
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: ""
        val password = intent.getStringExtra("EXTRA_PASSWORD") ?: ""

        binding.btnKirimKode.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                kirimKodeSms()
            } else {
                izinSms.launch(Manifest.permission.SEND_SMS)
            }
        }

        binding.btnVerifyOtp.setOnClickListener {
            val inputOtp = binding.etOtpInput.text.toString().trim()

            if (kodeTerkirim == null) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Belum Ada Kode")
                    .setMessage("Silakan klik tombol 'Kirim Kode via SMS' terlebih dahulu.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            if (inputOtp.isNotEmpty() && inputOtp == kodeTerkirim) {
                val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("reg_name", name)
                    putString("reg_phone", phone)
                    putString("reg_email", email)
                    putString("reg_username", username)
                    putString("reg_password", password)
                    apply()
                }

                Toast.makeText(this, "Verifikasi Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, login::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verifikasi Gagal")
                    .setMessage("Kode OTP SMS yang dimasukkan salah.")
                    .setPositiveButton("Coba Lagi") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    private fun generateDanTampilkanKode(): String {
        val kode = (100000..999999).random().toString()
        kodeTerkirim = kode
        binding.btnKirimKode.text = "Kirim Ulang Kode SMS"
        return kode
    }

    private fun kirimKodeSms() {
        val kode = generateDanTampilkanKode()
        try {
            ambilSmsManager().sendTextMessage(
                phone,
                null,
                "Kode verifikasi Member+ Anda: $kode",
                null,
                null
            )
            Toast.makeText(this, "Berhasil mengirim SMS ke $phone! (Kode: $kode)", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            MaterialAlertDialogBuilder(this)
                .setTitle("SMS Real Gagal Terkirim")
                .setMessage(
                    "Pesan SMS tidak dapat terkirim ke jaringan seluler.\n\n" +
                    "Penyebab utama:\n" +
                    "1. Menggunakan Emulator Android (tidak ada SIM asli / sinyal).\n" +
                    "2. Perangkat tidak memiliki Kartu SIM aktif atau tidak ada pulsa SMS.\n" +
                    "3. Format nomor HP bukan nomor seluler valid.\n\n" +
                    "Detail Error: ${e.localizedMessage ?: e.message}\n\n" +
                    "Kode OTP Anda untuk testing: $kode"
                )
                .setPositiveButton("OK", null)
                .show()
        }
    }

    @Suppress("DEPRECATION")
    private fun ambilSmsManager(): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val subId = SubscriptionManager.getDefaultSmsSubscriptionId()
                if (subId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    getSystemService(SmsManager::class.java).createForSubscriptionId(subId)
                } else {
                    getSystemService(SmsManager::class.java)
                }
            } catch (e: Exception) {
                SmsManager.getDefault()
            }
        } else {
            SmsManager.getDefault()
        }
    }
}
