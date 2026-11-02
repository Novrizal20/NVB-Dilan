package com.example.nvb_dilan.Voucher

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.nvb_dilan.BaseActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.FragmentVoucherBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VoucherFragment : Fragment() {
    private var _binding: FragmentVoucherBinding? = null
    private val binding get() = _binding!!

    // kartu yang diizinkan oleh kategori yang sedang dipilih
    private var kategoriTampil: List<View> = emptyList()

    // Data notifikasi yang menunggu hasil permintaan izin POST_NOTIFICATIONS (nama voucher, kode kupon)
    private var notifMenunggu: Pair<String, String>? = null
    private val izinNotifikasi = registerForActivityResult(ActivityResultContracts.RequestPermission()) { diberi ->
        if (diberi) notifMenunggu?.let { (nama, kode) -> kirimNotifikasi(nama, kode) }
        notifMenunggu = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.layoutToolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.title_voucher)
        }

        val semuaKartu = listOf(
            binding.cardVoucher1, binding.cardVoucher2, binding.cardVoucher3,
            binding.cardVoucher4, binding.cardVoucher5, binding.cardVoucher6
        )
        kategoriTampil = semuaKartu

        // Kategori: Semua Promo, Kopi, Kue & Roti, Paket Hemat
        binding.pillSemuaPromo.setOnClickListener { pilihKategori(binding.pillSemuaPromo, semuaKartu) }
        binding.pillKopi.setOnClickListener {
            pilihKategori(binding.pillKopi, listOf(binding.cardVoucher1, binding.cardVoucher2, binding.cardVoucher3))
        }
        binding.pillKue.setOnClickListener {
            pilihKategori(binding.pillKue, listOf(binding.cardVoucher4, binding.cardVoucher5))
        }
        binding.pillPaket.setOnClickListener {
            pilihKategori(binding.pillPaket, listOf(binding.cardVoucher2, binding.cardVoucher3, binding.cardVoucher6))
        }

        // Kolom cari: menyaring voucher setiap kali teks berubah
        binding.etCari.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                terapkanFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Klik banner / kartu voucher / tombol Tukar -> Detail Tukar Promo (Pop-Up Dialog)
        val v1Action = { showDetail("Kopi Kenangan - Voucher Diskon Rp 25.000", "2.500", "Berlaku untuk semua varian kopi di seluruh gerai Kopi Kenangan.") }
        val v2Action = { showDetail("Kopi Kenangan - Beli 1 Gratis 1 Kopi Susu", "1.200", "Berlaku untuk menu Kopi Susu di seluruh gerai Kopi Kenangan.") }
        val v3Action = { showDetail("Kopi Kenangan - Beli 1 Gratis 1 Latte", "1.800", "Berlaku untuk menu Latte di seluruh gerai Kopi Kenangan.") }
        val v4Action = { showDetail("Kopi Kenangan - Diskon 20% Mont Blanc", "1.500", "Berlaku untuk menu Mont Blanc di seluruh gerai Kopi Kenangan.") }
        val v5Action = { showDetail("Kopi Kenangan - Gratis 1 Croissant Almond", "2.200", "Berlaku untuk menu Croissant Almond di seluruh gerai Kopi Kenangan.") }
        val v6Action = { showDetail("Kopi Kenangan - Paket Kopi + Kue Rp 35.000", "3.000", "Berlaku untuk satu kopi dan satu kue pilihan di seluruh gerai Kopi Kenangan.") }

        binding.cardFlash.setOnClickListener { v1Action() }
        binding.cardVoucher1.setOnClickListener { v1Action() }
        binding.cardVoucher1Tukar.setOnClickListener { v1Action() }

        binding.cardVoucher2.setOnClickListener { v2Action() }
        binding.cardVoucher2Tukar.setOnClickListener { v2Action() }

        binding.cardVoucher3.setOnClickListener { v3Action() }
        binding.cardVoucher3Tukar.setOnClickListener { v3Action() }

        binding.cardVoucher4.setOnClickListener { v4Action() }
        binding.cardVoucher4Tukar.setOnClickListener { v4Action() }

        binding.cardVoucher5.setOnClickListener { v5Action() }
        binding.cardVoucher5Tukar.setOnClickListener { v5Action() }

        binding.cardVoucher6.setOnClickListener { v6Action() }
        binding.cardVoucher6Tukar.setOnClickListener { v6Action() }

        // Datang dari Dashboard (kartu menu kopi / kue / promo) -> kategori langsung terpilih
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        when (sharedPref.getString("kategori_voucher", null)) {
            "kopi" -> binding.pillKopi.performClick()
            "kue" -> binding.pillKue.performClick()
            "paket" -> binding.pillPaket.performClick()
        }
        sharedPref.edit().remove("kategori_voucher").apply()
    }

    private fun pilihKategori(aktif: TextView, tampil: List<View>) {
        kategoriTampil = tampil
        val semuaPill = listOf(binding.pillSemuaPromo, binding.pillKopi, binding.pillKue, binding.pillPaket)
        for (pill in semuaPill) {
            val on = pill == aktif
            pill.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), if (on) R.color.coffee_brown else R.color.white)
            )
            pill.setTextColor(ContextCompat.getColor(requireContext(), if (on) R.color.white else R.color.espresso))
        }
        terapkanFilter()
    }

    private fun terapkanFilter() {
        val kata = binding.etCari.text.toString().trim()
        val pasangan = listOf(
            binding.cardVoucher1 to binding.cardVoucher1Nama,
            binding.cardVoucher2 to binding.cardVoucher2Nama,
            binding.cardVoucher3 to binding.cardVoucher3Nama,
            binding.cardVoucher4 to binding.cardVoucher4Nama,
            binding.cardVoucher5 to binding.cardVoucher5Nama,
            binding.cardVoucher6 to binding.cardVoucher6Nama
        )
        var adaYangTampil = false
        for ((kartu, nama) in pasangan) {
            val cocok = kartu in kategoriTampil && (kata.isEmpty() || nama.text.contains(kata, ignoreCase = true))
            kartu.visibility = if (cocok) View.VISIBLE else View.GONE
            if (cocok) adaYangTampil = true
        }
        binding.tvVoucherKosong.visibility = if (adaYangTampil) View.GONE else View.VISIBLE
    }

    private fun showDetail(nama: String, poin: String, berlaku: String) {
        // Masa berlaku voucher: 30 hari dari hari penukaran, dihitung otomatis
        val batas = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 30) }
        val tanggalBerlaku = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")).format(batas.time)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Tukar Poin")
            .setMessage(
                "$nama\n\n" +
                        "Harga Penukaran: $poin Poin\n" +
                        "Saldo Poin Anda: 7.450 Poin\n\n" +
                        "Syarat & Ketentuan:\n" +
                        "1. Voucher berlaku hingga $tanggalBerlaku.\n" +
                        "2. $berlaku\n" +
                        "3. Maksimal penukaran 2 voucher per akun setiap minggunya."
            )
            .setPositiveButton("Tukar Sekarang") { dialog, _ ->
                dialog.dismiss()

                // Kode kupon dibuat SEKALI lalu dipakai di notifikasi dan dialog,
                // supaya keduanya menampilkan kode yang sama.
                val kodeKupon = buatKodeKupon()

                // 1. Kirim notifikasi sistem (status bar notification)
                notifikasiTukar(nama, kodeKupon)

                // 2. Tampilkan Pop-Up Dialog Berhasil (bukan hanya Toast/Snackbar)
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("🎉 Penukaran Poin Berhasil!")
                    .setMessage(
                        "Selamat! Penukaran poin berhasil dilakukan.\n\n" +
                                "Voucher: $nama\n" +
                                "Kode Kupon: $kodeKupon\n" +
                                "Poin Terpotong: -$poin Poin\n\n" +
                                "Tunjukkan QR Code di menu Member+ kepada kasir saat bertransaksi."
                    )
                    .setPositiveButton("Selesai", null)
                    .show()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun buatKodeKupon(): String = "KK-" + (100000..999999).random()

    private fun notifikasiTukar(nama: String, kodeKupon: String) {
        val perluIzin = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        if (perluIzin) {
            notifMenunggu = nama to kodeKupon
            izinNotifikasi.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            kirimNotifikasi(nama, kodeKupon)
        }
    }

    private fun kirimNotifikasi(nama: String, kodeKupon: String) {
        val ctx = context ?: return
        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "voucher_member"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Voucher Member+", NotificationManager.IMPORTANCE_HIGH)
            )
        }

        val intent = Intent(ctx, BaseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notifikasi = NotificationCompat.Builder(ctx, channelId)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
