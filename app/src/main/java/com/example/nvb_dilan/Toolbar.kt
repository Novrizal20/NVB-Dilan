package com.example.nvb_dilan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
fun AppCompatActivity.setupToolbar(toolbar: Toolbar, title: String, showBack: Boolean = true) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply {
        this.title = title
        if (showBack) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
    }
    if (showBack) {
        // tombol back di toolbar = sama dengan menekan tombol back sistem
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
