package com.airposted.bitoronbd_deliveryman.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentMyDeliveryHistoryBinding

class MyDeliveryHistoryFragment : Fragment() {
    private lateinit var binding: FragmentMyDeliveryHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyDeliveryHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BindUI()
    }

    private fun BindUI() {
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.my_delivery_history)


    }
}