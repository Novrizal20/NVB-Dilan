package com.example.nvb_dilan.Dashboard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.FragmentDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.layoutToolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.title_dashboard)
        }

        // Foto profil di header -> pindah ke tab Profil lewat bottom navigation
        binding.imgProfile.setOnClickListener { goToTab(R.id.profil) }

        // Kartu Gold: tombol "Eksklusif Gold" -> AlertDialog keuntungan member Gold
        binding.btnEksklusif.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Keuntungan Member Gold")
                .setMessage("1. Cashback 5% untuk setiap transaksi.\n2. Promo kopi dan kue eksklusif member Gold.\n3. Poin dapat ditukar voucher di Katalog Promo.")
                .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Tantangan Hari Ini -> tab Poin
        binding.cardTantangan.setOnClickListener { goToTab(R.id.point) }

        // Promo Eksklusif Gold -> tab Voucher (kategori Paket Hemat), "Lihat Semua" -> semua promo
        binding.cardPromoEks.setOnClickListener { bukaVoucher("paket") }
        binding.tvLihatSemuaPromo.setOnClickListener { goToTab(R.id.voucher) }

        // Menu Kopi -> Voucher kategori Kopi
        listOf(binding.cardKopi1, binding.cardKopi2, binding.cardKopi3, binding.cardKopi4).forEach { kartu ->
            kartu.setOnClickListener { bukaVoucher("kopi") }
        }
        // Kue & Roti -> Voucher kategori Kue & Roti
        listOf(binding.cardKue1, binding.cardKue2, binding.cardKue3, binding.cardKue4).forEach { kartu ->
            kartu.setOnClickListener { bukaVoucher("kue") }
        }
    }

    // Simpan kategori yang diminta, lalu pindah ke tab Voucher (dibaca oleh VoucherFragment)
    private fun bukaVoucher(kategori: String) {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        sharedPref.edit().putString("kategori_voucher", kategori).apply()
        goToTab(R.id.voucher)
    }

    private fun goToTab(itemId: Int) {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).selectedItemId = itemId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
