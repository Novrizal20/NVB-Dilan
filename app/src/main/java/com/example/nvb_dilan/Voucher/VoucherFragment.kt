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

        // Klik banner / kartu voucher / tombol Tukar -> Buka DetailPromoActivity
        val v1Action = { showDetail("Kopi Kenangan - Voucher Diskon Rp 25.000", 2500, "Berlaku untuk semua varian kopi di seluruh gerai Kopi Kenangan.") }
        val v2Action = { showDetail("Kopi Kenangan - Beli 1 Gratis 1 Kopi Susu", 1200, "Berlaku untuk menu Kopi Susu di seluruh gerai Kopi Kenangan.") }
        val v3Action = { showDetail("Kopi Kenangan - Beli 1 Gratis 1 Latte", 1800, "Berlaku untuk menu Latte di seluruh gerai Kopi Kenangan.") }
        val v4Action = { showDetail("Kopi Kenangan - Diskon 20% Mont Blanc", 1500, "Berlaku untuk menu Mont Blanc di seluruh gerai Kopi Kenangan.") }
        val v5Action = { showDetail("Kopi Kenangan - Gratis 1 Croissant Almond", 2200, "Berlaku untuk menu Croissant Almond di seluruh gerai Kopi Kenangan.") }
        val v6Action = { showDetail("Kopi Kenangan - Paket Kopi + Kue Rp 35.000", 3000, "Berlaku untuk satu kopi dan satu kue pilihan di seluruh gerai Kopi Kenangan.") }

        binding.cardFlash.setOnClickListener { v1Action() }

        val setupCardClick = { card: View, action: () -> Unit, voucherModel: VoucherModel ->
            card.setOnClickListener { action() }
            if (isAdmin) {
                card.setOnLongClickListener {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Kelola Voucher (Admin)")
                        .setItems(arrayOf("Edit Voucher", "Hapus Voucher")) { _, which ->
                            if (which == 0) {
                                dialogTambahAtauEditVoucher(voucherModel)
                            } else {
                                dialogKonfirmasiHapus(voucherModel)
                            }
                        }
                        .show()
                    true
                }
            }
        }

        setupCardClick(
            binding.cardVoucher1, v1Action,
            VoucherModel(1L, "Kopi Kenangan - Voucher Diskon Rp 25.000", "kopi", 2500, "30 Hari", "Berlaku untuk semua varian kopi di seluruh gerai Kopi Kenangan.")
        )
        setupCardClick(
            binding.cardVoucher2, v2Action,
            VoucherModel(2L, "Kopi Kenangan - Beli 1 Gratis 1 Kopi Susu", "kopi", 1200, "30 Hari", "Berlaku untuk menu Kopi Susu di seluruh gerai Kopi Kenangan.")
        )
        setupCardClick(
            binding.cardVoucher3, v3Action,
            VoucherModel(3L, "Kopi Kenangan - Beli 1 Gratis 1 Latte", "kopi", 1800, "30 Hari", "Berlaku untuk menu Latte di seluruh gerai Kopi Kenangan.")
        )
        setupCardClick(
            binding.cardVoucher4, v4Action,
            VoucherModel(4L, "Kopi Kenangan - Diskon 20% Mont Blanc", "kue", 1500, "30 Hari", "Berlaku untuk menu Mont Blanc di seluruh gerai Kopi Kenangan.")
        )
        setupCardClick(
            binding.cardVoucher5, v5Action,
            VoucherModel(5L, "Kopi Kenangan - Gratis 1 Croissant Almond", "kue", 2200, "30 Hari", "Berlaku untuk menu Croissant Almond di seluruh gerai Kopi Kenangan.")
        )
        setupCardClick(
            binding.cardVoucher6, v6Action,
            VoucherModel(6L, "Kopi Kenangan - Paket Kopi + Kue Rp 35.000", "paket", 3000, "30 Hari", "Berlaku untuk satu kopi dan satu kue pilihan di seluruh gerai Kopi Kenangan.")
        )

        binding.cardVoucher1Tukar.setOnClickListener { v1Action() }
        binding.cardVoucher2Tukar.setOnClickListener { v2Action() }
        binding.cardVoucher3Tukar.setOnClickListener { v3Action() }
        binding.cardVoucher4Tukar.setOnClickListener { v4Action() }
        binding.cardVoucher5Tukar.setOnClickListener { v5Action() }
        binding.cardVoucher6Tukar.setOnClickListener { v6Action() }

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
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val listVouchers = response.body()!!
                    Toast.makeText(context, "Terhubung ke Supabase (${listVouchers.size} voucher)", Toast.LENGTH_SHORT).show()
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
            hint = "Masa Berlaku (misal: 30 Hari)"
            setText(voucherOld?.masa_berlaku ?: "30 Hari")
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
                val masa = etMasaBerlaku.text.toString().trim()
                val desc = etDesc.text.toString().trim()

                if (nama.isEmpty()) {
                    Toast.makeText(ctx, "Nama voucher harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val voucherNew = VoucherModel(
                    id = voucherOld?.id,
                    nama_voucher = nama,
                    kategori = kat,
                    poin_dibutuhkan = poinInt,
                    masa_berlaku = masa,
                    deskripsi = desc
                )

                lifecycleScope.launch {
                    try {
                        val response = if (isEdit) {
                            SupabaseClient.instance.updateVoucher("eq.${voucherOld?.id}", voucherNew)
                        } else {
                            SupabaseClient.instance.createVoucher(voucherNew)
                        }

                        if (response.isSuccessful) {
                            Toast.makeText(ctx, if (isEdit) "Voucher diperbarui!" else "Voucher ditambahkan!", Toast.LENGTH_SHORT).show()
                            muatVoucherSupabase()
                        } else {
                            Toast.makeText(ctx, "Gagal simpan (Code ${response.code()})", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(requireContext(), "Gagal hapus (Code ${response.code()})", Toast.LENGTH_SHORT).show()
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
