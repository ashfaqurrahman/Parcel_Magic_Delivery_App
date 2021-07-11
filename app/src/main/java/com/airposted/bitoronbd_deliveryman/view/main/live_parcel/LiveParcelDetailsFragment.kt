package com.airposted.bitoronbd_deliveryman.view.main.live_parcel

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.MalformedJsonException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentLiveParcelDetailsBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.*
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class LiveParcelDetailsFragment : Fragment(), KodeinAware, IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentLiveParcelDetailsBinding
    private var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    var otp1 = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveParcelDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
            binding.status.dismiss()
        }
        binding.toolbar.toolbarTitle.text = "Order From " + requireArguments().getString("sender_name")

        binding.deliveryDate.text = requireArguments().getString("delivery_date")
        binding.quantity.text = requireArguments().getInt("item_qty").toString()
        binding.distance.text = requireArguments().getDouble("distance").toString() + " Km"
        binding.deliveryCharge.text = "BDT " + requireArguments().getDouble("delivery_charge").toString()
        binding.from.text = requireArguments().getString("pick_address")
        binding.to.text = requireArguments().getString("recp_address")
        binding.receiverName.text = requireArguments().getString("recp_name")
        binding.receiverPhone.text = requireArguments().getString("recp_phone")
        binding.senderName.text = requireArguments().getString("sender_name")
        binding.senderPhone.text = requireArguments().getString("sender_phone")

        when(requireArguments().getInt("item_type")) {
            1 -> {
                binding.size.text = getString(R.string.envelope_size1)
                binding.icon.setBackgroundResource(R.drawable.ic_document_large_icon)
            }
            2 -> {
                binding.size.text = getString(R.string.small_size1)
                binding.icon.setBackgroundResource(R.drawable.ic_box_large_icon)
            }
            3 -> {
                binding.size.text = getString(R.string.large_size1)
                binding.icon.setBackgroundResource(R.drawable.ic_box_large_icon)
            }
        }

        binding.calling.setOnClickListener {
            when(requireArguments().getInt("current_status")) {
                3 -> {
                    val number = requireArguments().getString("sender_phone")
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$number")
                    startActivity(intent)
                }
                4 -> {
                    val number = requireArguments().getString("recp_phone")
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$number")
                    startActivity(intent)
                }
            }
        }

//        binding.rootLayout.snackbar(requireArguments().getString("invoice")!!)

        when(requireArguments().getInt("current_status")) {
            3 -> {
                binding.receiverNameCalling.text = requireArguments().getString("sender_name")
                binding.status.selectItemByIndex(0)
            }
            4 -> {
                binding.receiverNameCalling.text = requireArguments().getString("recp_name")
                binding.status.selectItemByIndex(1)
            }
            5 -> {
                binding.receiverNameCalling.text = requireArguments().getString("recp_name")
                binding.status.selectItemByIndex(2)
            }
        }

        binding.status.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            when(newIndex) {
                0 -> {

                }
                1 -> {
                    val otpDialog = Dialog(requireContext())
                    otpDialog.setContentView(R.layout.otp_dialog)
                    otpDialog.findViewById<ImageView>(R.id.cancel).setOnClickListener {
                        otpDialog.dismiss()
                    }
                    otpDialog.findViewById<TextView>(R.id.title).text =
                        "Ask the sender for 6 digit \n OTP sent to his/her device"
                    val check = otpDialog.findViewById<CheckBox>(R.id.check)
                    if (requireArguments().getInt("coc") == 1) {
                        check.text =
                            "Collected " + requireArguments().getDouble("delivery_charge") + " Tk from sender"
                        check.visibility = View.VISIBLE
                    }
                    val verify = otpDialog.findViewById<TextView>(R.id.verify)
                    val otpView = otpDialog.findViewById<OtpTextView>(R.id.otp_view)
                    verify.isEnabled = false
                    otpView.otpListener = object : OTPListener {
                        override fun onInteractionListener() {
                            verify.isEnabled = false
                            verify.background = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.before_button_bg
                            )
                            otpView.resetState()
                        }

                        override fun onOTPComplete(otp: String) {
                            otp1 = otp
                            verify.background = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.after_button_bg
                            )
                            verify.isEnabled = true
                        }
                    }
                    verify.setOnClickListener {
                        if (requireArguments().getInt("coc") == 1) {
                            if (check.isChecked) {
                                changeStatus(otp1.toInt(), 4, otpDialog)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please confirm the checkbox",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            changeStatus(otp1.toInt(), 4, otpDialog)
                        }
                    }
                    otpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    otpDialog.window?.setLayout(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    otpDialog.setCancelable(true)
                    otpDialog.show()
                }
                2 -> {
                    val otpDialog = Dialog(requireContext())
                    otpDialog.setContentView(R.layout.otp_dialog)
                    otpDialog.findViewById<ImageView>(R.id.cancel).setOnClickListener {
                        otpDialog.dismiss()
                    }
                    otpDialog.findViewById<TextView>(R.id.title).text =
                        "Ask the receiver for 6 digit \n OTP sent to his/her device"
                    val check = otpDialog.findViewById<CheckBox>(R.id.check)
                    if (requireArguments().getInt("cod") == 1) {
                        check.text =
                            "Collected " + requireArguments().getDouble("delivery_charge") + " Tk from receiver"
                        check.visibility = View.VISIBLE
                    }
                    val verify = otpDialog.findViewById<TextView>(R.id.verify)
                    val otpView = otpDialog.findViewById<OtpTextView>(R.id.otp_view)
                    verify.isEnabled = false
                    otpView.otpListener = object : OTPListener {
                        override fun onInteractionListener() {
                            verify.isEnabled = false
                            verify.background = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.before_button_bg
                            )
                            otpView.resetState()
                        }

                        override fun onOTPComplete(otp: String) {
                            otp1 = otp
                            verify.background = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.after_button_bg
                            )
                            verify.isEnabled = true
                        }
                    }
                    verify.setOnClickListener {
                        if (requireArguments().getInt("cod") == 1) {
                            if (check.isChecked) {
                                changeStatus(otp1.toInt(), 5, otpDialog)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please confirm the checkbox",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            changeStatus(otp1.toInt(), 5, otpDialog)
                        }
                    }
                    otpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    otpDialog.window?.setLayout(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    otpDialog.setCancelable(false)
                    otpDialog.show()
                }
            }
        }
    }

    private fun changeStatus(otp: Int, status: Int, otpDialog: Dialog) {
        otpDialog.dismiss()
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.changeStatus(
                    requireArguments().getString("invoice")!!,
                    status,
                    otp
                )
                if (response.success) {
                    if (status == 5) {
                        deliveryComplete()
                    }
                    otpDialog.dismiss()
                    binding.rootLayout.snackbar(response.msg)
                } else {
                    binding.rootLayout.snackbar(response.msg)
                }
                dismissDialog()
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

    private fun deliveryComplete() {
        val otpDialog = Dialog(requireContext())
        otpDialog.setContentView(R.layout.delivery_complete_dialog)
        otpDialog.findViewById<TextView>(R.id.done).setOnClickListener {
            otpDialog.dismiss()
            if (requireArguments().getInt("coc") == 0 && requireArguments().getInt("cod") == 0) {
                val fragment = CompleteJourneyWithDigitalPaymentFragment()
                val bundle = Bundle()
                bundle.putDouble("delivery_charge", requireArguments().getDouble("delivery_charge"))
                fragment.arguments = bundle
                communicatorFragmentInterface!!.addContentFragment(fragment, false)
            } else {
                val fragment = CompleteJourneyFragment()
                val bundle = Bundle()
                bundle.putDouble("delivery_charge", requireArguments().getDouble("delivery_charge"))
                fragment.arguments = bundle
                communicatorFragmentInterface!!.addContentFragment(fragment, false)
            }
        }
        otpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        otpDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        otpDialog.setCancelable(false)
        otpDialog.show()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}