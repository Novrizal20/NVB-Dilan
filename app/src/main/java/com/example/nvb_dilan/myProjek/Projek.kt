package com.example.nvb_dilan.myProjek

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityProjekBinding
import com.example.nvb_dilan.setupToolbar

class Projek : AppCompatActivity() {
    private lateinit var binding: ActivityProjekBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_projek)
        binding = ActivityProjekBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_projek))
    }
}