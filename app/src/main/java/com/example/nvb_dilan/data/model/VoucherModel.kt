package com.example.nvb_dilan.data.model

data class VoucherModel(
    val id: Long? = null,
    val nama_voucher: String,
    val merchant: String? = "Kopi Kenangan",
    val kategori: String,
    val poin_dibutuhkan: Int,
    val masa_berlaku: String,
    val deskripsi: String
)
