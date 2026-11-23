package com.example.nvb_dilan.History

data class HistoryModel(
    val id: Long? = null,
    val userId: Long = 1L,
    val judul: String,
    val tanggal: String,
    val nominal: String,
    val poinText: String,
    val isMasuk: Boolean,
    var catatan: String? = null
)
