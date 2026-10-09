package com.example.nvb_dilan.myProjek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.MainActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityDashboardBinding
import com.example.nvb_dilan.setupToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_dashboard), showBack = false)

        binding.tvGreeting.text = "Hai, Novrizal Van Bastian!"

        binding.iconPoin.setOnClickListener {
            startActivity(Intent(this, point::class.java))
        }

        // Satu-satunya tombol keluar: klik ikon profil
        binding.imgProfile.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Keluar akun?")
                .setMessage("Kamu akan keluar dari Member+ Hub dan harus login lagi.")
                .setPositiveButton("Keluar") { _, _ ->
                    getSharedPreferences("user_pref", MODE_PRIVATE).edit().clear().apply()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
}