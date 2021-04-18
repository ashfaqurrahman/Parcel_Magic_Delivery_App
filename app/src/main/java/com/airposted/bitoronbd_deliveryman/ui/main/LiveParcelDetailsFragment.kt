package com.airposted.bitoronbd_deliveryman.ui.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentLiveParcelDetailsBinding


class LiveParcelDetailsFragment : Fragment() {
    private lateinit var binding: FragmentLiveParcelDetailsBinding
    private var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveParcelDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BindUI()
    }

    private fun BindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.order_from_person_name)

        binding.status.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            when(newIndex) {
                0 -> {

                }
                1 -> {
                    val otpDialog = Dialog(requireContext())
                    otpDialog.setContentView(R.layout.otp_dialog)
                    otpDialog.findViewById<TextView>(R.id.title).text = "Ask the sender for 6 digit \n OTP sent to his/her device"
                    otpDialog.findViewById<TextView>(R.id.verify).setOnClickListener {
                        otpDialog.dismiss()
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
                    otpDialog.findViewById<TextView>(R.id.title).text = "Ask the receiver for 6 digit \n OTP sent to his/her device"
                    otpDialog.findViewById<TextView>(R.id.verify).setOnClickListener {
                        deliveryComplete()
                        otpDialog.dismiss()
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

    private fun deliveryComplete() {
        val otpDialog = Dialog(requireContext())
        otpDialog.setContentView(R.layout.delivery_complete_dialog)
        otpDialog.findViewById<TextView>(R.id.done).setOnClickListener {
            otpDialog.dismiss()
            communicatorFragmentInterface!!.addContentFragment(CompleteJourneyFragment(), true)
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