package com.airposted.bitoronbd_deliveryman.view.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPhoneNumberBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class PhoneNumberFragment : Fragment() {
    private lateinit var binding: FragmentPhoneNumberBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var phone: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as AuthCommunicatorFragmentInterface
        binding.signIn.setOnClickListener {
            communicatorFragmentInterface?.addContentFragment(LoginFragment(), false)
        }
        textWatcher(requireContext(), 9, binding.phone, binding.next)
        binding.next.setOnClickListener {
            setProgressDialog(requireContext())
            phone = zeroRemove(binding.phone.text.toString().trim())


//            val fragment = OTPFragment()
//            val bundle = Bundle()
//            bundle.getString("phone", "+880$phone")
//            fragment.arguments = bundle
//            communicatorFragmentInterface?.addContentFragment(fragment, true)
        }
    }
}