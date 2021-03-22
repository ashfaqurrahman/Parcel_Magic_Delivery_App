package com.airposted.bitoronbd_deliveryman.ui.main

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentDefaultBinding

class DefaultFragment : Fragment() {
    private lateinit var binding: FragmentDefaultBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDefaultBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        return binding.root
    }

}