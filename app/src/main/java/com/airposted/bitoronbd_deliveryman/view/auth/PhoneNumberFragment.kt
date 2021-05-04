package com.airposted.bitoronbd_deliveryman.view.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.data.network.preferences.PreferenceProvider
import com.airposted.bitoronbd_deliveryman.data.network.responses.AuthResponse
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPhoneNumberBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

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
        if (PreferenceProvider(requireContext()).getSharedPreferences("phone") != null) {
            binding.phone.setText(PreferenceProvider(requireContext()).getSharedPreferences("phone"))
            binding.next.background = getDrawable(requireContext(), R.drawable.after_button_bg)
            binding.next.isEnabled = true
        }
        textWatcher(requireContext(), 8, binding.phone, binding.next)
        binding.next.setOnClickListener {
            hideKeyboard(requireActivity())
            setProgressDialog(requireContext())
            PreferenceProvider(requireContext()).saveSharedPreferences("phone", binding.phone.text.toString().trim())
            lifecycleScope.launch {
                try {
                    authResponse = viewModel.checkNumber("+8801$phone")
                    if (authResponse?.data != null) {
                        dismissDialog()
                        val fragment = WelcomeFragment()
                        val bundle = Bundle()
                        bundle.putString("phone", authResponse!!.user?.phone)
                        bundle.putString("token", authResponse!!.data?.token)
                        bundle.putInt("id", authResponse!!.user!!.id)
                        bundle.putString("image", authResponse!!.user!!.image)
                        bundle.putString("name", authResponse!!.user!!.username)
                        bundle.putBoolean("isAuth", true)
                        fragment.arguments = bundle
                        communicatorFragmentInterface?.addContentFragment(fragment, true)
                    } else {
                        dismissDialog()
                        val fragment = RegisterFragment()
                        val bundle = Bundle()
                        bundle.putString("phone", "+880$phone")
                        fragment.arguments = bundle
                        communicatorFragmentInterface?.addContentFragment(fragment, true)
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

        }
    }
}