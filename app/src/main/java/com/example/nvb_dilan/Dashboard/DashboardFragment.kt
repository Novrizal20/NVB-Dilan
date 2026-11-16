package com.example.nvb_dilan.Dashboard

import android.content.Context
import android.content.Intent
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
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        // Promo Eksklusif Gold -> DetailMenuActivity
        binding.cardPromoEks.setOnClickListener {
            bukaDetailMenu("☕", "Beli 1 Gratis 1 Latte", "Khusus pembayaran menggunakan poin balance", "Rp 24.000", 24)
        }
        binding.tvLihatSemuaPromo.setOnClickListener { goToTab(R.id.voucher) }

        // Menu Kopi -> DetailMenuActivity
        binding.cardKopi1.setOnClickListener {
            bukaDetailMenu("☕", "Kopi Kenangan Mantan", "Kopi susu gula aren", "Rp 18.000", 18)
        }
        binding.cardKopi2.setOnClickListener {
            bukaDetailMenu("☕", "Caffe Latte", "Espresso dan susu steamed", "Rp 24.000", 24)
        }
        binding.cardKopi3.setOnClickListener {
            bukaDetailMenu("☕", "Cappuccino", "Foam lembut, rasa pekat", "Rp 24.000", 24)
        }
        binding.cardKopi4.setOnClickListener {
            bukaDetailMenu("☕", "Americano", "Espresso dan air panas", "Rp 20.000", 20)
        }

        // Kue & Roti -> DetailMenuActivity
        binding.cardKue1.setOnClickListener {
            bukaDetailMenu("🍰", "Mont Blanc", "Krim kastanye lembut", "Rp 28.000", 28)
        }
        binding.cardKue2.setOnClickListener {
            bukaDetailMenu("🥐", "Croissant Almond", "Renyah dengan isi almond", "Rp 22.000", 22)
        }
        binding.cardKue3.setOnClickListener {
            bukaDetailMenu("🍫", "Brownies Choco", "Cokelat pekat dan lembut", "Rp 18.000", 18)
        }
        binding.cardKue4.setOnClickListener {
            bukaDetailMenu("🍩", "Donat Gula", "Empuk, taburan gula halus", "Rp 12.000", 12)
        }
    }

    override fun onResume() {
        super.onResume()
        updateDataDashboard()
    }

    private fun updateDataDashboard() {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("reg_name", null) ?: sharedPref.getString("username", null) ?: "Novrizal"
        val saldo = sharedPref.getInt("saldo_poin", 7450)

        binding.tvGreeting.text = "Hai, $name!"

        val saldoFormatted = String.format(Locale("id", "ID"), "%,d", saldo).replace(',', '.')
        binding.tvPoinValue.text = saldoFormatted

        val rupiah = saldo * 10
        val rupiahFormatted = String.format(Locale("id", "ID"), "%,d", rupiah).replace(',', '.')
        binding.tvKonversiValue.text = "Rp $rupiahFormatted"

        // Perbarui Progress & Teks Tantangan Hari Ini secara dinamis
        val progressTantangan = sharedPref.getInt("progress_tantangan", 1)
        val isSelesai = progressTantangan >= 2

        if (isSelesai) {
            binding.tvProgressValue.text = "2 / 2 Cup (Selesai)"
            binding.progressBar.progress = 100
        } else {
            binding.tvProgressValue.text = "$progressTantangan / 2 Cup"
            binding.progressBar.progress = progressTantangan * 50
        }
    }

    private fun bukaDetailMenu(icon: String, nama: String, desc: String, harga: String, poin: Int) {
        val intent = Intent(requireContext(), DetailMenuActivity::class.java).apply {
            putExtra("EXTRA_ICON", icon)
            putExtra("EXTRA_NAMA", nama)
            putExtra("EXTRA_DESC", desc)
            putExtra("EXTRA_HARGA", harga)
            putExtra("EXTRA_POIN", poin)
        }
        startActivity(intent)
    }

    private fun goToTab(itemId: Int) {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).selectedItemId = itemId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
