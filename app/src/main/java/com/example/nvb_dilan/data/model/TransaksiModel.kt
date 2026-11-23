package com.example.nvb_dilan.data.model

data class TransaksiModel(
    val id: Long? = null,
    val user_id: Long,
    val judul_transaksi: String,
    val tipe: String,
    val poin: Int,
    val catatan: String? = null
)
