package com.example.nvb_dilan.Home.myProjek

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nvb_dilan.BaseActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.data.api.SupabaseClient
import com.example.nvb_dilan.data.model.UserModel
import com.example.nvb_dilan.databinding.ActivityLoginBinding
import com.example.nvb_dilan.setupToolbar
import com.example.nvb_dilan.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        // Pengaman: kalau sudah login, langsung ke area Member+ Hub
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, BaseActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.layoutToolbar.toolbar, getString(R.string.title_login))

        binding.btnSubmit.setOnClickListener {
            val usernameInput = binding.etEmail.text.toString().trim()
            val passwordInput = binding.etPassword.text.toString().trim()

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Username dan password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = SupabaseClient.instance.loginUser("eq.$usernameInput", "eq.$passwordInput")
                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        val user = response.body()!![0]

                        // Simpan ke SessionManager
                        sessionManager.saveUser(user)

                        // Simpan juga ke user_pref untuk kompatibilitas
                        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                        sharedPref.edit()
                            .putBoolean("isLogin", true)
                            .putString("username", user.username)
                            .putString("reg_name", user.nama_lengkap)
                            .putString("reg_email", user.email)
                            .putString("reg_phone", user.no_hp)
                            .putInt("saldo_poin", user.saldo_poin)
                            .apply()

                        Toast.makeText(this@login, "Selamat datang, ${user.nama_lengkap}!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@login, BaseActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        // Fallback lokal jika Supabase mengembalikan daftar kosong / offline
                        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                        val savedUser = sharedPref.getString("reg_username", null)
                        val savedPass = sharedPref.getString("reg_password", null)

                        val isRule1Valid = usernameInput == passwordInput
                        val isRule2Valid = !savedUser.isNullOrEmpty() && savedPass == passwordInput && usernameInput == savedUser

                        if (isRule1Valid || isRule2Valid) {
                            val fallbackUser = UserModel(
                                id = 1L,
                                nama_lengkap = sharedPref.getString("reg_name", usernameInput) ?: usernameInput,
                                username = usernameInput,
                                email = sharedPref.getString("reg_email", "$usernameInput@gmail.com") ?: "$usernameInput@gmail.com",
                                password = passwordInput,
                                no_hp = sharedPref.getString("reg_phone", "08123456789") ?: "08123456789",
                                saldo_poin = sharedPref.getInt("saldo_poin", 500),
                                level_member = "Gold Member",
                                role = if (usernameInput == "admin") "admin" else "user"
                            )
                            sessionManager.saveUser(fallbackUser)

                            sharedPref.edit().putBoolean("isLogin", true).putString("username", usernameInput).apply()

                            val intent = Intent(this@login, BaseActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            MaterialAlertDialogBuilder(this@login)
                                .setTitle("Login Gagal")
                                .setMessage("Username atau Password salah! Silakan coba lagi.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                } catch (e: Exception) {
                    // Fallback jika tidak ada koneksi internet
                    val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                    val savedUser = sharedPref.getString("reg_username", null)
                    val savedPass = sharedPref.getString("reg_password", null)

                    val isRule1Valid = usernameInput == passwordInput
                    val isRule2Valid = !savedUser.isNullOrEmpty() && savedPass == passwordInput && usernameInput == savedUser

                    if (isRule1Valid || isRule2Valid) {
                        val fallbackUser = UserModel(
                            id = 1L,
                            nama_lengkap = sharedPref.getString("reg_name", usernameInput) ?: usernameInput,
                            username = usernameInput,
                            email = sharedPref.getString("reg_email", "$usernameInput@gmail.com") ?: "$usernameInput@gmail.com",
                            password = passwordInput,
                            no_hp = sharedPref.getString("reg_phone", "08123456789") ?: "08123456789",
                            saldo_poin = sharedPref.getInt("saldo_poin", 500),
                            level_member = "Gold Member",
                            role = if (usernameInput == "admin") "admin" else "user"
                        )
                        sessionManager.saveUser(fallbackUser)

                        sharedPref.edit().putBoolean("isLogin", true).putString("username", usernameInput).apply()

                        val intent = Intent(this@login, BaseActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        MaterialAlertDialogBuilder(this@login)
                            .setTitle("Login Gagal")
                            .setMessage("Username atau Password salah! Silakan coba lagi.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            }
        }

        // Ke halaman pendaftaran (register)
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }
    }
}
