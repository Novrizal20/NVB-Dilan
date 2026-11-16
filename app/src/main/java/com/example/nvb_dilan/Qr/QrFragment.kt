package com.example.nvb_dilan.Qr

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.FragmentQrBinding
import java.util.Locale

class QrFragment : Fragment() {
    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.layoutToolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.title_qr)
        }
    }

    override fun onResume() {
        super.onResume()
        updateDataQr()
    }

    private fun updateDataQr() {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("reg_name", null) ?: sharedPref.getString("username", null) ?: "Novrizal"
        val saldo = sharedPref.getInt("saldo_poin", 7450)

        binding.tvQrNama.text = name

        val saldoFormatted = String.format(Locale("id", "ID"), "%,d", saldo).replace(',', '.')
        binding.tvSaldoValue.text = "$saldoFormatted Poin"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
