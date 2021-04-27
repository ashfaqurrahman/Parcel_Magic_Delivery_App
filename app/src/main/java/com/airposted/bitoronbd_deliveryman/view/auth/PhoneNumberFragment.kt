package com.airposted.bitoronbd_deliveryman.view.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airposted.bitoronbd_deliveryman.data.network.responses.AuthResponse
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPhoneNumberBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PhoneNumberFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private var authResponse: AuthResponse? = null
    private lateinit var binding: FragmentPhoneNumberBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var phone: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
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
            lifecycleScope.launch {
                try {
                    authResponse = viewModel.checkNumber(phone!!)
                    if (authResponse?.data != null) {
                        dismissDialog()
                        binding.main.snackbar("Login Successfully")
                    } else {
                        binding.main.snackbar("Unauthorised User")
                    }
                } catch (e: ApiException) {
                    dismissDialog()
                    binding.main.snackbar(e.message!!)
                    e.printStackTrace()
                } catch (e: NoInternetException) {
                    dismissDialog()
                    binding.main.snackbar(e.message!!)
                    e.printStackTrace()
                }
            }

//            val fragment = OTPFragment()
//            val bundle = Bundle()
//            bundle.getString("phone", "+880$phone")
//            fragment.arguments = bundle
//            communicatorFragmentInterface?.addContentFragment(fragment, true)
        }
    }
}