package com.example.nvb_dilan.Home.myProjek

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.ActivityItemHistoryBinding
import com.example.nvb_dilan.databinding.ActivityItemVoucherBinding

class ItemVoucher : AppCompatActivity() {
    private lateinit var binding: ActivityItemVoucherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}