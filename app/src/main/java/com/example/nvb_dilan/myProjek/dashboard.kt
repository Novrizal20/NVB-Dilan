package com.example.nvb_dilan.myProjek

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityDashboardBinding

class dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvGreeting.text = "Hai, Novrizal Van Bastian!"

        binding.iconPoin.setOnClickListener {
            Toast.makeText(this, "Menu Poin Diklik", Toast.LENGTH_SHORT).show()
        }
    }
}