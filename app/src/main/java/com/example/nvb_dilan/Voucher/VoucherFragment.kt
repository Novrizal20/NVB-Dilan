package com.example.nvb_dilan.Voucher

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.nvb_dilan.R
import com.example.nvb_dilan.data.api.SupabaseClient
import com.example.nvb_dilan.data.model.VoucherModel
import com.example.nvb_dilan.databinding.FragmentVoucherBinding
import com.example.nvb_dilan.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VoucherFragment : Fragment() {
    private var _binding: FragmentVoucherBinding? = null
    private val binding get() = _binding!!

    // kartu yang diizinkan oleh kategori yang sedang dipilih
    private var kategoriTampil: List<View> = emptyList()

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

        val sessionManager = SessionManager(requireContext())
        val isAdmin = sessionManager.isAdmin()

        // Tampilkan/sembunyikan tombol tambah voucher berdasarkan hak akses admin
        binding.fabTambahVoucher.visibility = if (isAdmin) View.VISIBLE else View.GONE
        binding.fabTambahVoucher.setOnClickListener {
            dialogTambahAtauEditVoucher(null)
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

        // Klik banner
        val v1Action = { showDetail("Kopi Kenangan - Voucher Diskon Rp 25.000", 2500, "Berlaku untuk semua varian kopi di seluruh gerai Kopi Kenangan.") }
        binding.cardFlash.setOnClickListener { v1Action() }

        // Datang dari Dashboard (kartu menu kopi / kue / promo) -> kategori langsung terpilih
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        when (sharedPref.getString("kategori_voucher", null)) {
            "kopi" -> binding.pillKopi.performClick()
            "kue" -> binding.pillKue.performClick()
            "paket" -> binding.pillPaket.performClick()
        }
        sharedPref.edit().remove("kategori_voucher").apply()

        // Muat daftar voucher dari Supabase
        muatVoucherSupabase()
    }

    private fun muatVoucherSupabase() {
        lifecycleScope.launch {
            try {
                val response = SupabaseClient.instance.getVouchers()
                if (response.isSuccessful && response.body() != null) {
                    val listVouchers = response.body()!!

                    val kartuList = listOf(
                        Triple(binding.cardVoucher1, binding.cardVoucher1Nama, binding.cardVoucher1Poin),
                        Triple(binding.cardVoucher2, binding.cardVoucher2Nama, binding.cardVoucher2Poin),
                        Triple(binding.cardVoucher3, binding.cardVoucher3Nama, binding.cardVoucher3Poin),
                        Triple(binding.cardVoucher4, binding.cardVoucher4Nama, binding.cardVoucher4Poin),
                        Triple(binding.cardVoucher5, binding.cardVoucher5Nama, binding.cardVoucher5Poin),
                        Triple(binding.cardVoucher6, binding.cardVoucher6Nama, binding.cardVoucher6Poin)
                    )

                    val sessionManager = SessionManager(requireContext())
                    val isAdmin = sessionManager.isAdmin()

                    for (i in kartuList.indices) {
                        val (card, tvNama, tvPoin) = kartuList[i]
                        if (i < listVouchers.size) {
                            val v = listVouchers[i]
                            card.visibility = View.VISIBLE
                            tvNama.text = v.nama_voucher
                            val poinFormatted = String.format(Locale("id", "ID"), "%,d", v.poin_dibutuhkan).replace(',', '.')
                            tvPoin.text = "★ $poinFormatted Poin"

                            val action = { showDetail(v.nama_voucher, v.poin_dibutuhkan, v.deskripsi) }
                            card.setOnClickListener { action() }

                            if (isAdmin) {
                                card.setOnLongClickListener {
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Kelola Voucher (Admin)")
                                        .setItems(arrayOf("Edit Voucher", "Hapus Voucher")) { _, which ->
                                            if (which == 0) {
                                                dialogTambahAtauEditVoucher(v)
                                            } else {
                                                dialogKonfirmasiHapus(v)
                                            }
                                        }
                                        .show()
                                    true
                                }
                            }
                        } else {
                            // Sembunyikan kartu jika item telah dihapus / tidak ada di Supabase
                            card.visibility = View.GONE
                        }
                    }

                    terapkanFilter()
                }
            } catch (e: Exception) {
                // Fallback offline
            }
        }
    }

    private fun dialogTambahAtauEditVoucher(voucherOld: VoucherModel?) {
        val isEdit = voucherOld != null
        val ctx = requireContext()

        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
        }

        val etNama = EditText(ctx).apply {
            hint = "Nama Voucher"
            setText(voucherOld?.nama_voucher ?: "")
        }
        val etKategori = EditText(ctx).apply {
            hint = "Kategori (kopi / kue / paket)"
            setText(voucherOld?.kategori ?: "kopi")
        }
        val etPoin = EditText(ctx).apply {
            hint = "Poin Dibutuhkan (misal: 2500)"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(voucherOld?.poin_dibutuhkan?.toString() ?: "2500")
        }
        val etMasaBerlaku = EditText(ctx).apply {
            hint = "Masa Berlaku Hari (misal: 25 atau 2025-05-30)"
            setText(voucherOld?.masa_berlaku ?: "30")
        }
        val etDesc = EditText(ctx).apply {
            hint = "Deskripsi Voucher"
            setText(voucherOld?.deskripsi ?: "Berlaku di seluruh gerai Kopi Kenangan.")
        }

        layout.addView(etNama)
        layout.addView(etKategori)
        layout.addView(etPoin)
        layout.addView(etMasaBerlaku)
        layout.addView(etDesc)

        MaterialAlertDialogBuilder(ctx)
            .setTitle(if (isEdit) "Edit Voucher (Admin)" else "Tambah Voucher Baru (Admin)")
            .setView(layout)
            .setPositiveButton("Simpan") { dialog, _ ->
                val nama = etNama.text.toString().trim()
                val kat = etKategori.text.toString().trim()
                val poinInt = etPoin.text.toString().trim().toIntOrNull() ?: 2500
                val masaInput = etMasaBerlaku.text.toString().trim()
                val desc = etDesc.text.toString().trim()

                if (nama.isEmpty()) {
                    Toast.makeText(ctx, "Nama voucher harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Format tanggal YYYY-MM-DD agar sesuai dengan tipe data DATE di Supabase
                val dateFormatted = if (masaInput.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
                    masaInput
                } else {
                    val days = masaInput.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 30
                    val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, days) }
                    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                }

                val body = mapOf<String, Any>(
                    "nama_voucher" to nama,
                    "merchant" to "Kopi Kenangan",
                    "kategori" to kat,
                    "poin_dibutuhkan" to poinInt,
                    "masa_berlaku" to dateFormatted,
                    "deskripsi" to desc
                )

                lifecycleScope.launch {
                    try {
                        val response = if (isEdit) {
                            SupabaseClient.instance.updateVoucher("eq.${voucherOld?.id}", body)
                        } else {
                            SupabaseClient.instance.createVoucher(body)
                        }

                        if (response.isSuccessful) {
                            Toast.makeText(ctx, if (isEdit) "Voucher diperbarui!" else "Voucher ditambahkan!", Toast.LENGTH_SHORT).show()
                            muatVoucherSupabase()
                        } else {
                            val errMessage = response.errorBody()?.string() ?: "Unknown error"
                            MaterialAlertDialogBuilder(ctx)
                                .setTitle("Gagal Simpan (Code ${response.code()})")
                                .setMessage("Pesan Detail dari Supabase:\n\n$errMessage")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun dialogKonfirmasiHapus(voucher: VoucherModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Voucher?")
            .setMessage("Apakah Anda yakin ingin menghapus voucher '${voucher.nama_voucher}'?")
            .setPositiveButton("Hapus") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val response = SupabaseClient.instance.deleteVoucher("eq.${voucher.id}")
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Voucher berhasil dihapus!", Toast.LENGTH_SHORT).show()
                            muatVoucherSupabase()
                        } else {
                            val errMessage = response.errorBody()?.string() ?: ""
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Gagal Hapus (Code ${response.code()})")
                                .setMessage("Pesan Detail dari Supabase:\n\n$errMessage")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
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

    private fun showDetail(nama: String, poin: Int, berlaku: String) {
        val intent = Intent(requireContext(), DetailPromoActivity::class.java).apply {
            putExtra("EXTRA_NAMA", nama)
            putExtra("EXTRA_POIN", poin)
            putExtra("EXTRA_BERLAKU", berlaku)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
