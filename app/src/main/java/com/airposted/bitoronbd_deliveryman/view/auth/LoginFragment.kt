package com.airposted.bitoronbd_deliveryman.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.databinding.FragmentLoginBinding
import com.airposted.bitoronbd_deliveryman.view.main.MainActivity
import com.airposted.bitoronbd_deliveryman.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var phone: String? = null
    private var password: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as AuthCommunicatorFragmentInterface
        multipleTextWatcher(requireContext(), binding.phone, binding.password, binding.signIn, 11, 6)
        phone = binding.phone.text.toString().trim()
        password = binding.password.text.toString().trim()
        binding.signIn.setOnClickListener {
            setProgressDialog(requireContext())
//            lifecycleScope.launch {
//                try {
//                    authResponse = viewModel.userLogin(phone!!, password!!)
//                    if (authResponse?.tokenType != null) {
//                        dismissDialog()
//                    } else {
//                        binding.main.snackbar("Unauthorised User")
//                    }
//                } catch (e: ApiException) {
//                    dismissDialog()
//                    binding.main.snackbar(e.message!!)
//                    e.printStackTrace()
//                } catch (e: NoInternetException) {
//                    dismissDialog()
//                    binding.main.snackbar(e.message!!)
//                    e.printStackTrace()
//                }
//            }
        }

        binding.createAccount.setOnClickListener {
            communicatorFragmentInterface?.addContentFragment(PhoneNumberFragment(), true)
        }
        binding.signIn.setOnClickListener {
            PersistentUser.getInstance().setLogin(requireContext())
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}