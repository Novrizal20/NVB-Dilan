package com.example.nvb_dilan.myProjek

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_dashboard))

        binding.tvGreeting.text = "Hai, Novrizal Van Bastian!"

        binding.iconPoin.setOnClickListener {
            startActivity(Intent(this, point::class.java))
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                tampilkanDialogKeluar()
            }
        })
    }

    private fun tampilkanDialogKeluar() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Keluar dari Dashboard?")
            .setMessage("Apakah Anda yakin ingin keluar dan kembali ke halaman awal?")
            .setPositiveButton("Yakin") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Tetap di sini") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }
}
