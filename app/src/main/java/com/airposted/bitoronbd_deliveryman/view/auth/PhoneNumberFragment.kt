package com.airposted.bitoronbd_deliveryman.view.auth

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.MalformedJsonException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPhoneNumberBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PhoneNumberFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
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
        textWatcher(requireContext(), 9, binding.phone, binding.next)
        binding.next.setOnClickListener {
            hideKeyboard(requireActivity())
            phone = zeroRemove(binding.phone.text.toString().trim())
            if (phone!!.length == 10) {
                setProgressDialog(requireContext())
                lifecycleScope.launch {
                    try {
                        val authResponse = viewModel.checkNumber("+880$phone")
                        if (authResponse.success == true) {
                            if (authResponse.msg == "User not registered") {
                                dismissDialog()
                                val fragment = RegisterFragment()
                                val bundle = Bundle()
                                bundle.putString("phone", "+880$phone")
                                fragment.arguments = bundle
                                communicatorFragmentInterface?.addContentFragment(fragment, true)
                            } else {
                                when(authResponse.user!!.active) {
                                    0 -> {
                                        dismissDialog()
                                        createAccountSuccessDialog()
                                    }
                                    1 -> {
                                        dismissDialog()
                                        val fragment = WelcomeFragment()
                                        val bundle = Bundle()
                                        bundle.putString("phone", authResponse.user!!.phone)
                                        bundle.putString("token", authResponse.data!!.token)
                                        bundle.putInt("id", authResponse.user.id!!)
                                        bundle.putString("image", authResponse.user.image)
                                        bundle.putString("name", authResponse.user.username)
                                        bundle.putBoolean("isAuth", true)
                                        fragment.arguments = bundle
                                        communicatorFragmentInterface?.addContentFragment(fragment, true)
                                    }
                                }
                            }
                        } else {
                            dismissDialog()
                            binding.main.snackbar(authResponse.msg!!)
                        }
                    } catch (e: MalformedJsonException) {
                        dismissDialog()
                        binding.main.snackbar(e.message!!)
                        e.printStackTrace()
                    } catch (e: JsonSyntaxException) {
                        dismissDialog()
                        binding.main.snackbar(e.message!!)
                        e.printStackTrace()
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
            else {
                binding.main.snackbar("Incorrect Phone Number")
            }
        }
    }

    private fun createAccountSuccessDialog() {
        val dialogs = Dialog(requireActivity())
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.create_account_dialog)
        dialogs.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogs.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,  //w
            ViewGroup.LayoutParams.MATCH_PARENT //h
        )
        val done = dialogs.findViewById<TextView>(R.id.done)
        done.setOnClickListener {
            dialogs.dismiss()
        }
        dialogs.setCancelable(false)
        dialogs.show()
    }
}