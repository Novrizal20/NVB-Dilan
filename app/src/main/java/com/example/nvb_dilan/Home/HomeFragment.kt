package com.example.nvb_dilan.Home

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nvb_dilan.Home.myProjek.History
import com.example.nvb_dilan.Home.myProjek.Profil
import com.example.nvb_dilan.Home.myProjek.Projek
import com.example.nvb_dilan.Home.myProjek.QR_Code
import com.example.nvb_dilan.Home.myProjek.Voucher
import com.example.nvb_dilan.Home.myProjek.WebViewActivity
import com.example.nvb_dilan.Home.myProjek.dashboard
import com.example.nvb_dilan.Home.myProjek.login
import com.example.nvb_dilan.R
import com.example.nvb_dilan.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.layoutToolbar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Home Fragment"
        }
        val sharedPref = requireContext().getSharedPreferences("user_pref", MODE_PRIVATE)
        binding.btnOpen.setOnClickListener {
            startActivity(Intent(requireContext(), Projek::class.java))
        }

        binding.btnMember.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("user_pref", MODE_PRIVATE)
            val isLogin = sharedPref.getBoolean("isLogin", false)

            if (isLogin) {
                startActivity(Intent(requireContext(), dashboard::class.java))
            } else {
                startActivity(Intent(requireContext(), login::class.java))
            }
        }
        binding.btnWebView.setOnClickListener {
            startActivity(Intent(requireContext(), WebViewActivity::class.java))
        }
        binding.btnProfil.setOnClickListener {
            startActivity(Intent(requireContext(), Profil::class.java))
        }
        binding.btnQrCode.setOnClickListener {
            val intent = Intent(requireContext(), QR_Code::class.java)
            startActivity(intent)
        }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(requireContext(), History::class.java)
            startActivity(intent)
        }

        binding.btnVoucher.setOnClickListener {
            val intent = Intent(requireContext(), Voucher::class.java)
            startActivity(intent)
        }
    }
}