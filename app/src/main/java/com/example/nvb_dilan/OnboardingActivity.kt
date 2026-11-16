package com.example.nvb_dilan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.nvb_dilan.Home.HomeActivity
import com.example.nvb_dilan.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var dots: Array<ImageView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            OnboardingItem(
                "Kumpulkan Poin Setiap Transaksi",
                "Dapatkan poin otomatis dan cashback 5% Gold Member di setiap pembelian kopi favorit.",
                R.drawable.logo_member
            ),
            OnboardingItem(
                "Tukar Voucher & Promo Eksklusif",
                "Tukarkan poinmu dengan berbagai diskon minuman, pastry, dan paket hemat.",
                R.drawable.ic_voucher
            ),
            OnboardingItem(
                "Scan QR Member Praktis",
                "Cukup tunjukkan QR Card digital di kasir untuk transaksi lebih cepat.",
                R.drawable.ic_qr_code
            )
        )

        onboardingAdapter = OnboardingAdapter(items)
        binding.viewPagerOnboarding.adapter = onboardingAdapter

        setupDots(items.size)
        setCurrentDot(0)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentDot(position)
                if (position == items.size - 1) {
                    binding.btnLanjut.text = "Mulai Sekarang"
                } else {
                    binding.btnLanjut.text = "Lanjut"
                }
            }
        })

        binding.btnLanjut.setOnClickListener {
            val current = binding.viewPagerOnboarding.currentItem
            if (current < items.size - 1) {
                binding.viewPagerOnboarding.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnLewati.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun setupDots(size: Int) {
        dots = arrayOfNulls(size)
        binding.layoutDots.removeAllViews()
        val margin = 8 * resources.displayMetrics.density.toInt()

        for (i in 0 until size) {
            dots[i] = ImageView(this).apply {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_circle_nutrition))
                layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    setMargins(margin, 0, margin, 0)
                }
            }
            binding.layoutDots.addView(dots[i])
        }
    }

    private fun setCurrentDot(index: Int) {
        for (i in dots.indices) {
            val dot = dots[i] ?: continue
            if (i == index) {
                dot.backgroundTintList = ContextCompat.getColorStateList(this, R.color.member_gold)
            } else {
                dot.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }
    }

    private fun finishOnboarding() {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        sharedPref.edit().putBoolean("is_onboarding_finished", true).apply()

        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
