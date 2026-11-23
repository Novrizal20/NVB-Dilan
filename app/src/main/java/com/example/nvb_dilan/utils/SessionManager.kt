package com.example.nvb_dilan.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.nvb_dilan.data.model.UserModel

class SessionManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGIN = "is_login"
        private const val KEY_ID = "id"
        private const val KEY_NAMA_LENGKAP = "nama_lengkap"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_NO_HP = "no_hp"
        private const val KEY_SALDO_POIN = "saldo_poin"
        private const val KEY_LEVEL_MEMBER = "level_member"
        private const val KEY_ROLE = "role"
    }

    fun saveUser(user: UserModel) {
        pref.edit().apply {
            putBoolean(KEY_IS_LOGIN, true)
            putLong(KEY_ID, user.id ?: 0L)
            putString(KEY_NAMA_LENGKAP, user.nama_lengkap)
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            putString(KEY_NO_HP, user.no_hp)
            putInt(KEY_SALDO_POIN, user.saldo_poin)
            putString(KEY_LEVEL_MEMBER, user.level_member)
            putString(KEY_ROLE, user.role)
            apply()
        }
    }

    fun getUser(): UserModel? {
        if (!isLoggedIn()) return null
        return UserModel(
            id = pref.getLong(KEY_ID, 0L),
            nama_lengkap = pref.getString(KEY_NAMA_LENGKAP, "") ?: "",
            username = pref.getString(KEY_USERNAME, "") ?: "",
            email = pref.getString(KEY_EMAIL, "") ?: "",
            password = "",
            no_hp = pref.getString(KEY_NO_HP, "") ?: "",
            saldo_poin = pref.getInt(KEY_SALDO_POIN, 500),
            level_member = pref.getString(KEY_LEVEL_MEMBER, "Gold Member") ?: "Gold Member",
            role = pref.getString(KEY_ROLE, "user") ?: "user"
        )
    }

    fun updateSaldoPoin(saldo: Int) {
        pref.edit().putInt(KEY_SALDO_POIN, saldo).apply()
    }

    fun isLoggedIn(): Boolean = pref.getBoolean(KEY_IS_LOGIN, false)

    fun isAdmin(): Boolean = pref.getString(KEY_ROLE, "user") == "admin"

    fun logout() {
        pref.edit().clear().apply()
    }
}
