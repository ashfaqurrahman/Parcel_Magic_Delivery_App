package com.airposted.bitoronbd_deliveryman.view.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.MalformedJsonException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentParcelDetailsBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ParcelDetailsFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentParcelDetailsBinding
    private var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    private var invoice = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParcelDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        binding.toolbar.toolbarTitle.text = requireArguments().getString("order_item_name")
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.deliveryTime.text = requireArguments().getString("delivery_date")
        binding.from.text = requireArguments().getString("pick_address")
        binding.to.text = requireArguments().getString("recp_address")
        invoice = requireArguments().getString("invoice")!!

        binding.confirmedOrder.setOnClickListener {

            val dialogs = Dialog(requireActivity())
            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogs.setContentView(R.layout.order_accept_dialog)
            dialogs.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogs.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,  //w
                ViewGroup.LayoutParams.MATCH_PARENT //h
            )

            val cancel = dialogs.findViewById<TextView>(R.id.cancel)
            val ok = dialogs.findViewById<TextView>(R.id.ok)
            cancel.setOnClickListener {
                dialogs.dismiss()
            }

            ok.setOnClickListener {
                dialogs.dismiss()
                setProgressDialog(requireActivity())
                lifecycleScope.launch {
                    try {
                        val response = viewModel.changeStatus(invoice, 3)
                        if (response.success) {
                            dismissDialog()
                            requireActivity().supportFragmentManager.popBackStack(ParcelRequestFragment::class.java.name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            communicatorFragmentInterface!!.addContentFragment(MyLiveDeliveryFragment(), true)
                        }
                        else {
                            binding.rootLayout.snackbar(response.msg)
                            dismissDialog()
                        }
                    } catch (e: MalformedJsonException) {
                        dismissDialog()
                        binding.rootLayout.snackbar(e.message!!)
                        e.printStackTrace()
                    } catch (e: ApiException) {
                        dismissDialog()
                        binding.rootLayout.snackbar(e.message!!)
                        e.printStackTrace()
                    } catch (e: NoInternetException) {
                        dismissDialog()
                        binding.rootLayout.snackbar(e.message!!)
                        e.printStackTrace()
                    }

                }
            }
        }
    }
}