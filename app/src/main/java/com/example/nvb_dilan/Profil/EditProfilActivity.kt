package com.example.nvb_dilan.Profil

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.databinding.ActivityEditProfilBinding
import com.example.nvb_dilan.setupToolbar

class EditProfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.layoutToolbar.toolbar, "Data Akun Diri")

        // Tampilkan informasi user terlebih dahulu (View Mode)
        tampilkanViewMode()

        // Tombol Edit Data Profil -> beralih ke Edit Mode
        binding.btnMulaiEdit.setOnClickListener {
            tampilkanEditMode()
        }

        // Tombol Batal -> kembali ke View Mode
        binding.btnBatal.setOnClickListener {
            tampilkanViewMode()
        }

        // Tombol Simpan Perubahan -> simpan dan kembali ke View Mode
        binding.btnSimpan.setOnClickListener {
            val name = binding.etNama.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Semua field data profil harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            sharedPref.edit()
                .putString("reg_name", name)
                .putString("reg_email", email)
                .putString("reg_phone", phone)
                .apply()

            Toast.makeText(this, "Data profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            tampilkanViewMode()
        }
    }

    private fun tampilkanViewMode() {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val currentName = sharedPref.getString("reg_name", null) ?: sharedPref.getString("username", null) ?: "Novrizal"
        val currentEmail = sharedPref.getString("reg_email", null) ?: "Novrizal@gmail.com"
        val currentPhone = sharedPref.getString("reg_phone", null) ?: "+62 821-4412-998"

        binding.tvViewNama.text = currentName
        binding.tvViewEmail.text = currentEmail
        binding.tvViewPhone.text = currentPhone

        binding.layoutViewMode.visibility = View.VISIBLE
        binding.layoutEditMode.visibility = View.GONE
    }

    private fun tampilkanEditMode() {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val currentName = sharedPref.getString("reg_name", null) ?: sharedPref.getString("username", null) ?: "Novrizal"
        val currentEmail = sharedPref.getString("reg_email", null) ?: "Novrizal@gmail.com"
        val currentPhone = sharedPref.getString("reg_phone", null) ?: "+62 821-4412-998"

        binding.etNama.setText(currentName)
        binding.etEmail.setText(currentEmail)
        binding.etPhone.setText(currentPhone)

        binding.layoutViewMode.visibility = View.GONE
        binding.layoutEditMode.visibility = View.VISIBLE
    }
}
