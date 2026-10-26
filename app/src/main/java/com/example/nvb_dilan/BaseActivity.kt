package com.example.nvb_dilan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.nvb_dilan.Dashboard.DashboardFragment
import com.example.nvb_dilan.History.HistoryFragment
import com.example.nvb_dilan.Home.HomeActivity
import com.example.nvb_dilan.Point.PointFragment
import com.example.nvb_dilan.Profil.ProfilFragment
import com.example.nvb_dilan.Qr.QrFragment
import com.example.nvb_dilan.Voucher.VoucherFragment
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
        // BaseActivity hanya untuk member: kalau belum login, kembali ke halaman awal
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        if (!sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }
        /** FragmentDashboard sebagai fragment default (hanya saat Activity pertama kali dibuat) */
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    replaceFragment(DashboardFragment())
                    true
                }
                R.id.point -> {
                    replaceFragment(PointFragment())
                    true
                }
                R.id.voucher -> {
                    replaceFragment(VoucherFragment())
                    true
                }
                R.id.qr -> {
                    replaceFragment(QrFragment())
                    true
                }
                R.id.profil -> {
                    replaceFragment(ProfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        // kosongkan back stack (mis. dari HistoryFragment) supaya Back tetap langsung keluar aplikasi
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            //.addToBackStack(null) -> ini kita nonaktifkan agar saat back langsung keluar aplikasi
            .commit()
    }

    /** HistoryFragment bukan tab bottom navigation: dibuka dari tombol di PointFragment, Back kembali ke sana. */
    fun showHistory() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, HistoryFragment())
            .addToBackStack(null)
            .commit()
    }
}
