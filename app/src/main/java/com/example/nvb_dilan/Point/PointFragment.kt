package com.example.nvb_dilan.Point

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nvb_dilan.BaseActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.FragmentPointBinding
import java.util.Locale

class PointFragment : Fragment() {
    private var _binding: FragmentPointBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPointBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.layoutToolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.title_poin_saya)
        }

        // Filter riwayat aktivitas poin: kartu yang tidak sesuai disembunyikan
        binding.pillSemua.setOnClickListener {
            filterRiwayat(binding.pillSemua, listOf(binding.cardRiwayat1, binding.cardRiwayat2, binding.cardRiwayat3))
        }
        binding.pillMasuk.setOnClickListener {
            filterRiwayat(binding.pillMasuk, listOf(binding.cardRiwayat1, binding.cardRiwayat3))
        }
        binding.pillKeluar.setOnClickListener {
            filterRiwayat(binding.pillKeluar, listOf(binding.cardRiwayat2))
        }

        // "Lihat Semua" -> Riwayat Transaksi (HistoryFragment bukan tab, dibuka lewat BaseActivity.showHistory())
        binding.tvLihatSemua.setOnClickListener { (requireActivity() as BaseActivity).showHistory() }
    }

    override fun onResume() {
        super.onResume()
        updateDataPoin()
    }

    private fun updateDataPoin() {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("reg_name", null) ?: sharedPref.getString("username", null) ?: "NOVRIZAL"
        val saldo = sharedPref.getInt("saldo_poin", 7450)

        binding.tvHeaderNama.text = "${name.uppercase(Locale.ROOT)} · GOLD"

        val saldoFormatted = String.format(Locale("id", "ID"), "%,d", saldo).replace(',', '.')
        binding.tvSaldoPoin.text = saldoFormatted

        val rupiah = saldo * 10
        val rupiahFormatted = String.format(Locale("id", "ID"), "%,d", rupiah).replace(',', '.')
        binding.tvSetaraRupiah.text = "Setara Rp $rupiahFormatted saldo belanja"

        binding.progressTier.max = 10000
        binding.progressTier.progress = saldo.coerceAtMost(10000)

        val sisa = (10000 - saldo).coerceAtLeast(0)
        val sisaFormatted = String.format(Locale("id", "ID"), "%,d", sisa).replace(',', '.')
        binding.tvSisaPoinLagi.text = "$sisaFormatted Poin Lagi"
    }

    private fun filterRiwayat(aktif: TextView, tampil: List<View>) {
        val semuaKartu = listOf(binding.cardRiwayat1, binding.cardRiwayat2, binding.cardRiwayat3)
        for (kartu in semuaKartu) {
            kartu.visibility = if (kartu in tampil) View.VISIBLE else View.GONE
        }
        val semuaPill = listOf(binding.pillSemua, binding.pillMasuk, binding.pillKeluar)
        for (pill in semuaPill) {
            val on = pill == aktif
            pill.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), if (on) R.color.coffee_brown else R.color.white)
            )
            pill.setTextColor(ContextCompat.getColor(requireContext(), if (on) R.color.white else R.color.espresso))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
