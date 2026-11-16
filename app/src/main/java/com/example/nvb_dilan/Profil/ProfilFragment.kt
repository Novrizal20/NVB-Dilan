package com.example.nvb_dilan.Profil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.Home.HomeActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.FragmentProfilBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.layoutToolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.title_profil_saya)
        }

        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        // Klik Data Akun Diri -> Buka EditProfilActivity
        binding.rowDataAkun.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfilActivity::class.java))
        }
        binding.rowNotifikasi.setOnClickListener {
            val aktif = sharedPref.getBoolean("notif", true)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Manajemen Notifikasi")
                .setMessage("Notifikasi poin dan voucher saat ini: " + if (aktif) "Aktif" else "Nonaktif")
                .setPositiveButton("Aktifkan") { dialog, _ ->
                    sharedPref.edit().putBoolean("notif", true).apply()
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Notifikasi diaktifkan", Snackbar.LENGTH_SHORT).setAction("Tutup") { }.show()
                }
                .setNegativeButton("Matikan") { dialog, _ ->
                    sharedPref.edit().putBoolean("notif", false).apply()
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Notifikasi dimatikan", Snackbar.LENGTH_SHORT).setAction("Tutup") { }.show()
                }
                .show()
        }
        binding.rowBahasa.setOnClickListener {
            val bahasa = sharedPref.getString("bahasa", "Indonesia")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilihan Bahasa")
                .setMessage("Bahasa saat ini: $bahasa")
                .setPositiveButton("Indonesia") { dialog, _ ->
                    sharedPref.edit().putString("bahasa", "Indonesia").apply()
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Bahasa diubah ke Indonesia", Snackbar.LENGTH_SHORT).setAction("Tutup") { }.show()
                }
                .setNegativeButton("English") { dialog, _ ->
                    sharedPref.edit().putString("bahasa", "English").apply()
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Bahasa diubah ke English", Snackbar.LENGTH_SHORT).setAction("Tutup") { }.show()
                }
                .show()
        }
        binding.rowSandi.setOnClickListener {
            showInfo("Keamanan Kata Sandi", "Kata sandi akun diatur saat login. Fitur ubah kata sandi masuk roadmap Fase 2.")
        }
        binding.rowBantuan.setOnClickListener {
            showInfo("Pusat Bantuan Pelanggan", "Butuh bantuan penukaran poin atau transaksi? Hubungi kasir di outlet terdekat atau baca Syarat & Ketentuan Penggunaan.")
        }
        binding.rowSyarat.setOnClickListener {
            showInfo(
                "Syarat & Ketentuan Penggunaan",
                "1. Poin diperoleh dari setiap transaksi di merchant partner.\n" +
                "2. Poin dapat ditukar dengan voucher di Katalog Promo.\n" +
                "3. Voucher tidak dapat diuangkan atau digabungkan dengan promo cash back lain.\n" +
                "4. Maksimal penukaran 2 voucher per akun setiap minggunya."
            )
        }
        binding.rowPrivasi.setOnClickListener {
            showInfo("Kebijakan Privasi Data", "Data akun dan riwayat transaksi hanya dipakai untuk program Member+ dan tidak dibagikan tanpa persetujuanmu. (Teks contoh)")
        }

        // Satu-satunya tombol keluar
        binding.btnKeluar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Keluar akun?")
                .setMessage("Kamu akan keluar dari Member+ Hub dan harus login lagi.")
                .setPositiveButton("Keluar") { _, _ ->
                    // hapus status login saja (akun hasil registrasi tetap tersimpan)
                    sharedPref.edit().remove("isLogin").remove("username").apply()

                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    // posisi di Fragment -> destroy BaseActivity lewat requireActivity()
                    requireActivity().finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateDataProfil()
    }

    private fun updateDataProfil() {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("reg_name", null) ?: sharedPref.getString("username", null) ?: "Novrizal"
        val email = sharedPref.getString("reg_email", null) ?: "Novrizal@gmail.com"
        val phone = sharedPref.getString("reg_phone", null) ?: "+62 821-4412-998"

        binding.tvNamaProfil.text = name
        binding.tvKontak.text = "$email • $phone"

        val saldo = sharedPref.getInt("saldo_poin", 7450)
        val saldoFormatted = String.format(Locale("id", "ID"), "%,d", saldo).replace(',', '.')

        binding.tvStatPoin.text = saldoFormatted
        binding.tvKemajuanNilai.text = "$saldoFormatted / 10.000 Poin"

        binding.progressKemajuan.max = 10000
        binding.progressKemajuan.progress = saldo.coerceAtMost(10000)

        val sisa = (10000 - saldo).coerceAtLeast(0)
        val sisaFormatted = String.format(Locale("id", "ID"), "%,d", sisa).replace(',', '.')
        binding.tvKemajuanLabel.text = "Menuju Platinum ($sisaFormatted Poin lagi)"
    }

    private fun showInfo(judul: String, pesan: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(judul)
            .setMessage(pesan)
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
