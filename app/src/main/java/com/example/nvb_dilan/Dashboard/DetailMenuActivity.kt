package com.example.nvb_dilan.Dashboard

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nvb_dilan.data.api.SupabaseClient
import com.example.nvb_dilan.data.model.TransaksiModel
import com.example.nvb_dilan.databinding.ActivityDetailMenuBinding
import com.example.nvb_dilan.setupToolbar
import com.example.nvb_dilan.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.Locale

class DetailMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMenuBinding

    private var jumlah = 1
    private var hargaSatuanNum = 18000
    private var poinSatuan = 18

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.layoutToolbar.toolbar, "Detail Menu")

        // Membaca data Intent
        val icon = intent.getStringExtra("EXTRA_ICON") ?: "☕"
        val nama = intent.getStringExtra("EXTRA_NAMA") ?: "Kopi Kenangan Mantan"
        val desc = intent.getStringExtra("EXTRA_DESC") ?: "Kopi susu gula aren"
        val hargaStr = intent.getStringExtra("EXTRA_HARGA") ?: "Rp 18.000"
        poinSatuan = intent.getIntExtra("EXTRA_POIN", 18)

        // Parse harga satuan ke angka
        hargaSatuanNum = hargaStr.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 18000

        binding.tvMenuIcon.text = icon
        binding.tvNamaMenu.text = nama
        binding.tvDescMenu.text = desc
        binding.tvHargaMenu.text = hargaStr
        binding.tvBadgePoinMasuk.text = "+$poinSatuan Poin"

        updateTotals()

        binding.btnMinus.setOnClickListener {
            if (jumlah > 1) {
                jumlah--
                updateTotals()
            }
        }

        binding.btnPlus.setOnClickListener {
            jumlah++
            updateTotals()
        }

        binding.btnPesan.setOnClickListener {
            val sessionManager = SessionManager(this)
            val user = sessionManager.getUser()

            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val saldoAwal = user?.saldo_poin ?: sharedPref.getInt("saldo_poin", 7450)

            val totalPoinDapat = poinSatuan * jumlah

            // Poin & Progress Tantangan Hari Ini (Beli 2 Cup Kopi)
            val isKopi = nama.contains("Kopi", ignoreCase = true) ||
                    nama.contains("Latte", ignoreCase = true) ||
                    nama.contains("Cappuccino", ignoreCase = true) ||
                    nama.contains("Americano", ignoreCase = true)

            var bonusTantangan = 0
            var tantanganBaruSelesai = false

            if (isKopi) {
                val progressAwal = sharedPref.getInt("progress_tantangan", 1)
                val progressBaru = (progressAwal + jumlah).coerceAtMost(2)
                val sudahKlaim = sharedPref.getBoolean("tantangan_claimed", false)

                if (progressBaru >= 2 && !sudahKlaim) {
                    bonusTantangan = 200
                    tantanganBaruSelesai = true
                    sharedPref.edit()
                        .putInt("progress_tantangan", 2)
                        .putBoolean("tantangan_claimed", true)
                        .apply()
                } else {
                    sharedPref.edit().putInt("progress_tantangan", progressBaru).apply()
                }
            }

            val totalPoinMasuk = totalPoinDapat + bonusTantangan
            val saldoBaru = saldoAwal + totalPoinMasuk

            // Simpan saldo poin baru ke SessionManager & SharedPreferences
            sessionManager.updateSaldoPoin(saldoBaru)
            sharedPref.edit().putInt("saldo_poin", saldoBaru).apply()

            val totalHargaNum = hargaSatuanNum * jumlah
            val totalHargaStr = "Rp " + String.format(Locale("id", "ID"), "%,d", totalHargaNum).replace(',', '.')
            val saldoBaruFormatted = String.format(Locale("id", "ID"), "%,d", saldoBaru).replace(',', '.')

            // Simpan transaksi baru & update saldo di Supabase REST API (CREATE)
            lifecycleScope.launch {
                try {
                    val userId = user?.id ?: 1L
                    SupabaseClient.instance.updateSaldoPoin("eq.$userId", mapOf("saldo_poin" to saldoBaru))

                    val transaksi = TransaksiModel(
                        user_id = userId,
                        judul_transaksi = nama,
                        tipe = "DAPAT_POIN",
                        poin = totalPoinMasuk,
                        catatan = "Pesanan ${jumlah}x $nama ($totalHargaStr)"
                    )
                    SupabaseClient.instance.createTransaksi(transaksi)
                } catch (e: Exception) {
                    // Fallback offline
                }
            }

            val pesanSukses = if (tantanganBaruSelesai) {
                "Terima kasih telah memesan $nama (${jumlah}x)!\n\n" +
                "Total Pembayaran: $totalHargaStr\n" +
                "Poin Pesanan: +$totalPoinDapat Poin\n" +
                "🎉 Bonus Tantangan Selesai: +200 Poin\n" +
                "Saldo Poin Baru: $saldoBaruFormatted Poin\n\n" +
                "Poin dan bonus telah ditambahkan ke akun kamu!"
            } else {
                "Terima kasih telah memesan $nama (${jumlah}x)!\n\n" +
                "Total Pembayaran: $totalHargaStr\n" +
                "Poin Diperoleh: +$totalPoinDapat Poin\n" +
                "Saldo Poin Baru: $saldoBaruFormatted Poin\n\n" +
                "Poin telah ditambahkan ke akun kamu."
            }

            MaterialAlertDialogBuilder(this)
                .setTitle("🎉 Pesanan Berhasil!")
                .setMessage(pesanSukses)
                .setPositiveButton("Selesai") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        }
    }

    private fun updateTotals() {
        val totalPoin = poinSatuan * jumlah
        val totalHargaNum = hargaSatuanNum * jumlah
        val totalHargaStr = "Rp " + String.format(Locale("id", "ID"), "%,d", totalHargaNum).replace(',', '.')

        binding.tvJumlah.text = jumlah.toString()
        binding.tvTotalHarga.text = totalHargaStr
        binding.tvTotalPoin.text = "+$totalPoin Poin"
    }
}
