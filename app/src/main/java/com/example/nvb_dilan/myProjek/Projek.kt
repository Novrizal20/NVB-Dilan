package com.example.nvb_dilan.myProjek

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityProjekBinding

class Projek : AppCompatActivity() {
    private lateinit var binding: ActivityProjekBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_projek)
        binding = ActivityProjekBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}