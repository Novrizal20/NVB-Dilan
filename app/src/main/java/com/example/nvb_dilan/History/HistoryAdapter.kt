package com.example.nvb_dilan.History

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nvb_dilan.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoryAdapter(
    private val historyList: List<HistoryModel>,
    private val onEditCatatan: (HistoryModel) -> Unit,
    private val onDeleteItem: (HistoryModel) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvCatatan: TextView = view.findViewById(R.id.tvCatatan)
        val tvNominal: TextView = view.findViewById(R.id.tvNominal)
        val tvPoin: TextView = view.findViewById(R.id.tvPoin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_card, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.tvJudul.text = item.judul
        holder.tvTanggal.text = item.tanggal
        holder.tvNominal.text = item.nominal
        holder.tvPoin.text = item.poinText

        if (!item.catatan.isNullOrEmpty()) {
            holder.tvCatatan.visibility = View.VISIBLE
            holder.tvCatatan.text = "Catatan: ${item.catatan}"
        } else {
            holder.tvCatatan.visibility = View.GONE
        }

        val context = holder.itemView.context
        if (item.isMasuk) {
            holder.ivIcon.setImageResource(R.drawable.ic_history)
            holder.ivIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
            holder.tvPoin.setTextColor(ContextCompat.getColor(context, R.color.poin_masuk))
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_voucher)
            holder.ivIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FCE4EC"))
            holder.tvPoin.setTextColor(ContextCompat.getColor(context, R.color.poin_keluar))
        }

        // Opsi Edit Catatan / Hapus saat item diklik atau ditekan lama
        holder.itemView.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Kelola Transaksi")
                .setMessage("Pilih tindakan untuk riwayat transaksi ini:")
                .setPositiveButton("Edit Catatan") { dialog, _ ->
                    onEditCatatan(item)
                    dialog.dismiss()
                }
                .setNegativeButton("Hapus") { dialog, _ ->
                    onDeleteItem(item)
                    dialog.dismiss()
                }
                .setNeutralButton("Batal", null)
                .show()
        }
    }

    override fun getItemCount(): Int = historyList.size
}
