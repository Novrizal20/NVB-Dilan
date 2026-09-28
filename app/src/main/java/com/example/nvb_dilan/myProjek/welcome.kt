package com.example.nvb_dilan.myProjek

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityDashboardBinding
import com.example.nvb_dilan.databinding.ActivityWelcomeBinding

class welcome : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMasuk.setOnClickListener {
            val intent = Intent(this, dashboard::class.java)
            startActivity(intent)
        }

        binding.btnLewati.setOnClickListener {
            val intent = Intent(this, dashboard::class.java)
            startActivity(intent)
        }
    }
}