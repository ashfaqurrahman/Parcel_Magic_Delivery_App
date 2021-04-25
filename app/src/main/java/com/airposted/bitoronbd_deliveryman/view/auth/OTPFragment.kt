package com.airposted.bitoronbd_deliveryman.view.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentOTPBinding
import com.airposted.bitoronbd_deliveryman.utils.otpWatcher

class OTPFragment : Fragment() {
    private lateinit var binding: FragmentOTPBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var phone: String? = null
    var otp1: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.toolbar.toolbarTitle.text = getString(R.string.verification)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        communicatorFragmentInterface = context as AuthCommunicatorFragmentInterface
        phone = requireArguments().getString("phone")
        otp1 = otpWatcher(requireContext(), binding.otpView, binding.verify)
        binding.verify.setOnClickListener {
            val fragment = RegisterFragment()
            val bundle = Bundle()
            bundle.getString("phone", phone)
            fragment.arguments = bundle
            communicatorFragmentInterface?.addContentFragment(fragment, true)
        }
    }
}