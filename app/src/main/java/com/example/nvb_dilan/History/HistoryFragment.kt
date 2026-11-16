package com.example.nvb_dilan.History

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.FragmentHistoryBinding

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

        // Daftar transaksi lengkap bulan April 2025
        val historyList = listOf(
            HistoryModel("Kopi Kenangan - MKG 3", "15 Apr 2025, 14:10", "Rp 75.000", "+150 Poin", true),
            HistoryModel("Kopi Kenangan - Sudirman", "14 Apr 2025, 08:45", "Rp 45.000", "+90 Poin", true),
            HistoryModel("Kopi Kenangan - Grand Indonesia", "13 Apr 2025, 13:05", "Rp 60.000", "+120 Poin", true),
            HistoryModel("Kopi Kenangan - Kelapa Gading", "12 Apr 2025, 11:32", "Rp 225.000", "+450 Poin", true),
            HistoryModel("Kopi Kenangan - Pacific Place", "11 Apr 2025, 17:30", "Rp 150.000", "+300 Poin", true),
            HistoryModel("Penukaran Voucher Kopi Diskon Rp 25.000", "10 Apr 2025, 16:45", "Voucher Redeem", "-2.500 Poin", false),
            HistoryModel("Kopi Kenangan - Kelapa Gading", "09 Apr 2025, 12:10", "Rp 190.000", "+380 Poin", true),
            HistoryModel("Kopi Kenangan - Grand Indonesia", "08 Apr 2025, 19:15", "Rp 320.000", "+640 Poin", true),
            HistoryModel("Kopi Kenangan - MKG 3", "07 Apr 2025, 10:25", "Rp 55.000", "+110 Poin", true),
            HistoryModel("Kopi Kenangan - Senayan City", "05 Apr 2025, 15:40", "Rp 210.000", "+420 Poin", true),
            HistoryModel("Kopi Kenangan - Kelapa Gading", "03 Apr 2025, 09:15", "Rp 35.000", "+70 Poin", true),
            HistoryModel("Kopi Kenangan - Grand Indonesia", "01 Apr 2025, 18:50", "Rp 85.000", "+170 Poin", true)
        )

        // Setup RecyclerView
        binding.rvHistoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryList.adapter = HistoryAdapter(historyList)

        // Tampilkan jumlah transaksi secara dinamis
        binding.tvJumlahTransaksi.text = "${historyList.size} Transaksi"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
