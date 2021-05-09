package com.airposted.bitoronbd_deliveryman.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airposted.bitoronbd_deliveryman.databinding.FragmentCompleteJourneyBinding

class CompleteJourneyFragment : Fragment(),IOnBackPressed {
    private lateinit var binding: FragmentCompleteJourneyBinding
    private var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompleteJourneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface

        binding.cost.text = "৳" + requireArguments().getInt("price")

        binding.completeYourJourney.setOnClickListener {
            communicatorFragmentInterface?.addContentFragment(HomeFragment(), false)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}