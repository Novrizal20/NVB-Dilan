package com.example.nvb_dilan.data.model

data class VoucherModel(
    val id: Long? = null,
    val nama_voucher: String,
    val kategori: String,
    val poin_dibutuhkan: Int,
    val masa_berlaku: String,
    val deskripsi: String
)
