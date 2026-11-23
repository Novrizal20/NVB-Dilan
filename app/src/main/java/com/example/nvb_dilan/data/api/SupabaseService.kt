package com.example.nvb_dilan.data.api

import com.example.nvb_dilan.data.model.TransaksiModel
import com.example.nvb_dilan.data.model.UserModel
import com.example.nvb_dilan.data.model.VoucherModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseService {
    companion object {
        const val API_KEY = "sb_publishable_l8nqH-z30rG0a4gjKrGlfw_DZtC2tyb"
    }

    // ================= USERS =================
    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @POST("users")
    suspend fun registerUser(
        @Body user: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile"
    )
    @GET("users")
    suspend fun loginUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<List<UserModel>>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @PATCH("users")
    suspend fun updateSaldoPoin(
        @Query("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    // ================= VOUCHERS =================
    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile"
    )
    @GET("vouchers")
    suspend fun getVouchers(): Response<List<VoucherModel>>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @POST("vouchers")
    suspend fun createVoucher(
        @Body voucher: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @PATCH("vouchers")
    suspend fun updateVoucher(
        @Query("id") id: String,
        @Body voucher: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @DELETE("vouchers")
    suspend fun deleteVoucher(
        @Query("id") id: String
    ): Response<Unit>

    // ================= TRANSAKSI_POIN =================
    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile"
    )
    @GET("transaksi_poin")
    suspend fun getTransaksi(
        @Query("user_id") userId: String
    ): Response<List<TransaksiModel>>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @POST("transaksi_poin")
    suspend fun createTransaksi(
        @Body transaksi: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @PATCH("transaksi_poin")
    suspend fun updateTransaksi(
        @Query("id") id: String,
        @Body transaksi: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Accept-Profile: mobile",
        "Content-Profile: mobile",
        "Prefer: return=minimal"
    )
    @DELETE("transaksi_poin")
    suspend fun deleteTransaksi(
        @Query("id") id: String
    ): Response<Unit>
}
