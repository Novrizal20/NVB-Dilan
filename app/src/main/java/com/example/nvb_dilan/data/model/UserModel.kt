package com.example.nvb_dilan.data.model

data class UserModel(
    val id: Long? = null,
    val nama_lengkap: String,
    val username: String,
    val email: String,
    val password: String,
    val no_hp: String,
    val saldo_poin: Int = 500,
    val level_member: String = "Gold Member",
    val role: String = "user"
)
