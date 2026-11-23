package com.example.nvb_dilan.History

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nvb_dilan.R
import com.example.nvb_dilan.data.api.SupabaseClient
import com.example.nvb_dilan.databinding.FragmentHistoryBinding
import com.example.nvb_dilan.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.layoutToolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.title_riwayat)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
        binding.layoutToolbar.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rvHistoryList.layoutManager = LinearLayoutManager(requireContext())

        // READ: Muat transaksi dari Supabase
        muatTransaksiSupabase()
    }

    private fun muatTransaksiSupabase() {
        val sessionManager = SessionManager(requireContext())
        val user = sessionManager.getUser()
        val userId = user?.id ?: 1L

        lifecycleScope.launch {
            try {
                val response = SupabaseClient.instance.getTransaksi("eq.$userId")
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val listData = response.body()!!.map { item ->
                        HistoryModel(
                            id = item.id,
                            userId = item.user_id,
                            judul = item.judul_transaksi,
                            tanggal = "Hari ini",
                            nominal = if (item.tipe == "TUKAR_VOUCHER") "Voucher Redeem" else "Pesanan Selesai",
                            poinText = if (item.tipe == "TUKAR_VOUCHER") "-${item.poin} Poin" else "+${item.poin} Poin",
                            isMasuk = item.tipe != "TUKAR_VOUCHER",
                            catatan = item.catatan
                        )
                    }
                    tampilkanList(listData)
                } else {
                    tampilkanDefaultDemo()
                }
            } catch (e: Exception) {
                tampilkanDefaultDemo()
            }
        }
    }

    private fun tampilkanList(historyList: List<HistoryModel>) {
        binding.rvHistoryList.adapter = HistoryAdapter(
            historyList,
            onEditCatatan = { item -> dialogEditCatatan(item) },
            onDeleteItem = { item -> dialogHapusTransaksi(item) }
        )
        binding.tvJumlahTransaksi.text = "${historyList.size} Transaksi"
    }

    private fun tampilkanDefaultDemo() {
        val defaultList = listOf(
            HistoryModel(1L, 1L, "Kopi Kenangan - MKG 3", "15 Apr 2025, 14:10", "Rp 75.000", "+150 Poin", true, "Pembelian 2x Kopi Mantan"),
            HistoryModel(2L, 1L, "Kopi Kenangan - Sudirman", "14 Apr 2025, 08:45", "Rp 45.000", "+90 Poin", true, "Pembelian 1x Latte"),
            HistoryModel(3L, 1L, "Kopi Kenangan - Grand Indonesia", "13 Apr 2025, 13:05", "Rp 60.000", "+120 Poin", true, "Pembelian 2x Americano"),
            HistoryModel(4L, 1L, "Penukaran Voucher Diskon Rp 25.000", "10 Apr 2025, 16:45", "Voucher Redeem", "-2.500 Poin", false, "Voucher berhasil ditukar")
        )
        tampilkanList(defaultList)
    }

    private fun dialogEditCatatan(item: HistoryModel) {
        val ctx = requireContext()
        val etCatatan = EditText(ctx).apply {
            hint = "Tulis catatan baru..."
            setText(item.catatan ?: "")
        }

        MaterialAlertDialogBuilder(ctx)
            .setTitle("Edit Catatan Transaksi")
            .setView(etCatatan)
            .setPositiveButton("Simpan") { dialog, _ ->
                val catatanBaru = etCatatan.text.toString().trim()
                lifecycleScope.launch {
                    try {
                        val body = mapOf<String, Any>(
                            "catatan" to catatanBaru
                        )
                        val response = SupabaseClient.instance.updateTransaksi("eq.${item.id}", body)
                        if (response.isSuccessful) {
                            Toast.makeText(ctx, "Catatan transaksi diperbarui!", Toast.LENGTH_SHORT).show()
                            muatTransaksiSupabase()
                        } else {
                            Toast.makeText(ctx, "Gagal update (Code ${response.code()})", Toast.LENGTH_SHORT).show()
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

    private fun dialogHapusTransaksi(item: HistoryModel) {
        val ctx = requireContext()
        MaterialAlertDialogBuilder(ctx)
            .setTitle("Hapus Riwayat Transaksi?")
            .setMessage("Apakah Anda yakin ingin menghapus transaksi '${item.judul}'?")
            .setPositiveButton("Hapus") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val response = SupabaseClient.instance.deleteTransaksi("eq.${item.id}")
                        if (response.isSuccessful) {
                            Toast.makeText(ctx, "Transaksi berhasil dihapus!", Toast.LENGTH_SHORT).show()
                            muatTransaksiSupabase()
                        } else {
                            Toast.makeText(ctx, "Gagal hapus (Code ${response.code()})", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
