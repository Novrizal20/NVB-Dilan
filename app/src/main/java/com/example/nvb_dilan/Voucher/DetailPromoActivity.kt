package com.example.nvb_dilan.Voucher

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.nvb_dilan.BaseActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.data.api.SupabaseClient
import com.example.nvb_dilan.data.model.TransaksiModel
import com.example.nvb_dilan.databinding.ActivityDetailPromoBinding
import com.example.nvb_dilan.setupToolbar
import com.example.nvb_dilan.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class DetailPromoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPromoBinding

    private var notifMenunggu: Pair<String, String>? = null
    private val izinNotifikasi = registerForActivityResult(ActivityResultContracts.RequestPermission()) { diberi ->
        if (diberi) notifMenunggu?.let { (nama, kode) -> kirimNotifikasi(nama, kode) }
        notifMenunggu = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.layoutToolbar.toolbar, "Detail Promo")

        val namaVoucher = intent.getStringExtra("EXTRA_NAMA") ?: "Voucher Kopi Kenangan"
        val hargaPoin = intent.getIntExtra("EXTRA_POIN", 2500)
        val berlakuVoucher = intent.getStringExtra("EXTRA_BERLAKU") ?: "Berlaku di seluruh outlet Kopi Kenangan."

        val sessionManager = SessionManager(this)
        val userSession = sessionManager.getUser()

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val saldoSaatIni = userSession?.saldo_poin ?: sharedPref.getInt("saldo_poin", 7450)

        val poinFormatted = String.format(Locale("id", "ID"), "%,d", hargaPoin).replace(',', '.')
        val saldoFormatted = String.format(Locale("id", "ID"), "%,d", saldoSaatIni).replace(',', '.')

        binding.tvBannerTitle.text = namaVoucher
        binding.tvNamaVoucher.text = namaVoucher
        binding.tvBadgePoin.text = "★ $poinFormatted Poin"
        binding.tvDeskripsiVoucher.text = berlakuVoucher
        binding.tvSaldoKamu.text = "$saldoFormatted Poin"

        val batas = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 30) }
        val tanggalBerlaku = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")).format(batas.time)

        // Modul 10: ListView menggunakan SimpleAdapter untuk Syarat & Ketentuan / Cara Penggunaan
        val rulesList = ArrayList<HashMap<String, String>>()
        rulesList.add(hashMapOf("judul" to "Masa Berlaku", "keterangan" to "Voucher berlaku hingga $tanggalBerlaku."))
        rulesList.add(hashMapOf("judul" to "Lokasi Penukaran", "keterangan" to berlakuVoucher))
        rulesList.add(hashMapOf("judul" to "Ketentuan Promo", "keterangan" to "Tidak dapat diuangkan atau digabungkan dengan promo cash back lain."))
        rulesList.add(hashMapOf("judul" to "Batas Penggunaan", "keterangan" to "Maksimal penukaran 2 voucher per akun setiap minggunya."))
        rulesList.add(hashMapOf("judul" to "Cara Pakai di Kasir", "keterangan" to "Tunjukkan QR Code kupon pada kasir saat memesan untuk potongan otomatis."))

        val adapter = SimpleAdapter(
            this,
            rulesList,
            android.R.layout.simple_list_item_2,
            arrayOf("judul", "keterangan"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        binding.lvSyaratKetentuan.adapter = adapter

        binding.lvSyaratKetentuan.setOnItemClickListener { _, _, position, _ ->
            val item = rulesList[position]
            Toast.makeText(this, "${item["judul"]}: ${item["keterangan"]}", Toast.LENGTH_SHORT).show()
        }

        binding.btnKonfirmasiTukar.setOnClickListener {
            val currentUser = sessionManager.getUser()
            val latestSaldo = currentUser?.saldo_poin ?: sharedPref.getInt("saldo_poin", 7450)
            val latestSaldoFormatted = String.format(Locale("id", "ID"), "%,d", latestSaldo).replace(',', '.')

            // Cek kecukupan saldo poin
            if (latestSaldo < hargaPoin) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Saldo Poin Tidak Cukup")
                    .setMessage("Saldo poin kamu saat ini ($latestSaldoFormatted Poin) tidak mencukupi untuk menukar voucher ini ($poinFormatted Poin). Yuk tingkatkan transaksi kamu!")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            // Kurangi saldo poin & simpan ke SessionManager & SharedPreferences
            val sisaPoin = latestSaldo - hargaPoin
            sessionManager.updateSaldoPoin(sisaPoin)
            sharedPref.edit().putInt("saldo_poin", sisaPoin).apply()

            val sisaFormatted = String.format(Locale("id", "ID"), "%,d", sisaPoin).replace(',', '.')
            binding.tvSaldoKamu.text = "$sisaFormatted Poin"

            val kodeKupon = "KK-" + (100000..999999).random()

            // Update Supabase API & catat transaksi penukaran voucher
            lifecycleScope.launch {
                try {
                    val userId = currentUser?.id ?: 1L
                    SupabaseClient.instance.updateSaldoPoin("eq.$userId", mapOf("saldo_poin" to sisaPoin))

                    val transaksi = TransaksiModel(
                        user_id = userId,
                        judul_transaksi = "Penukaran $namaVoucher",
                        tipe = "TUKAR_VOUCHER",
                        poin = hargaPoin,
                        catatan = "Kode Kupon: $kodeKupon"
                    )
                    SupabaseClient.instance.createTransaksi(transaksi)
                } catch (e: Exception) {
                    // Fallback
                }
            }

            // Cek pengaturan notifikasi
            val notifAktif = sharedPref.getBoolean("notif", true)
            if (notifAktif) {
                notifikasiTukar(namaVoucher, kodeKupon)
            }

            // Tampilkan Dialog Berhasil
            MaterialAlertDialogBuilder(this)
                .setTitle("🎉 Penukaran Poin Berhasil!")
                .setMessage(
                    "Selamat! Penukaran poin berhasil dilakukan.\n\n" +
                    "Voucher: $namaVoucher\n" +
                    "Kode Kupon: $kodeKupon\n" +
                    "Poin Terpotong: -$poinFormatted Poin\n" +
                    "Sisa Saldo Poin: $sisaFormatted Poin\n\n" +
                    "Tunjukkan QR Code di menu Member+ kepada kasir saat bertransaksi."
                )
                .setPositiveButton("Selesai") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        }
    }

    private fun notifikasiTukar(nama: String, kodeKupon: String) {
        val perluIzin = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        if (perluIzin) {
            notifMenunggu = nama to kodeKupon
            izinNotifikasi.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            kirimNotifikasi(nama, kodeKupon)
        }
    }

    private fun kirimNotifikasi(nama: String, kodeKupon: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "voucher_member"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Voucher Member+", NotificationManager.IMPORTANCE_HIGH)
            )
        }

        val intent = Intent(this, BaseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notifikasi = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notif_voucher)
            .setContentTitle("Voucher berhasil ditukar")
            .setContentText("$nama | Kode kupon: $kodeKupon")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$nama\nKode kupon: $kodeKupon\nTunjukkan QR Member ke kasir saat memesan."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notifikasi)
    }
}
