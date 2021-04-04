package com.airposted.bitoronbd_deliveryman.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airposted.bitoronbd_deliveryman.databinding.FragmentMyWalletBinding

class MyWalletFragment : Fragment() {
    private lateinit var binding: FragmentMyWalletBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BindUI()
    }

    private fun BindUI() {
        binding.customToolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.customToolbar.toolbarTitle.text = "My Wallet"
    }
}


