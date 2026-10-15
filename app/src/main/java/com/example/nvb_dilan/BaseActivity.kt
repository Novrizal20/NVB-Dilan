package com.example.nvb_dilan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.nvb_dilan.Home.HomeFragment
import com.example.nvb_dilan.Home.myProjek.dashboard
import com.example.nvb_dilan.Message.MessageFragment
import com.example.nvb_dilan.More.MoreFragment
import com.example.nvb_dilan.databinding.ActivityBaseBinding

class BaseActivity : AppCompatActivity() {
    private lateinit var binding : ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, dashboard::class.java))
            finish()
            return
        }
        /** FragmentHome sebagai fragment default */
        replaceFragment(HomeFragment())

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.message -> {
                    replaceFragment(MessageFragment())
                    true
                }
                R.id.more -> {
                    replaceFragment(MoreFragment())
                    true
                }
                else ->false
            }
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            //.addToBackStack(null) -> ini kita nonaktifkan agar saat back langsung keluar aplikasi
            .commit()
    }
}