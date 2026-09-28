package com.example.nvb_dilan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.databinding.ActivityMainBinding
import com.example.nvb_dilan.myProjek.Projek

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnOpen.setOnClickListener {
            val intent = Intent(this, Projek::class.java)
            startActivity(intent)
        }
    }
}