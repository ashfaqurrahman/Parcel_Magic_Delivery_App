package com.airposted.bitoronbd_deliveryman.ui.auth

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.ActivityAuthBinding
import com.airposted.bitoronbd_deliveryman.ui.main.MainActivity
import com.airposted.bitoronbd_deliveryman.utils.*

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private var phone: String? = null
    var otp1: String? = null
    private var timer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)

        binding.signInLayout.main.visibility = View.VISIBLE
        binding.mobileNumberLayout.main.visibility = View.GONE
        binding.otpLayout.main.visibility = View.GONE
        binding.personalInformationLayout.main.visibility = View.GONE

        multipleTextWatcher(this, binding.signInLayout.phone, binding.signInLayout.password, binding.signInLayout.signIn, 11, 6)

        binding.signInLayout.signIn.setOnClickListener {
            PersistentUser.getInstance().setLogin(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.signInLayout.createAccount.setOnClickListener {
            binding.signInLayout.main.visibility = View.GONE
            binding.mobileNumberLayout.main.visibility = View.VISIBLE
            binding.otpLayout.main.visibility = View.GONE
            binding.personalInformationLayout.main.visibility = View.GONE
            textWatcher(this, 9, binding.mobileNumberLayout.phone, binding.mobileNumberLayout.next)
        }

        binding.mobileNumberLayout.next.setOnClickListener {
            hideKeyboard(this)
            phone = zeroRemove(binding.mobileNumberLayout.phone.text.toString().trim())
            binding.signInLayout.main.visibility = View.GONE
            binding.mobileNumberLayout.main.visibility = View.GONE
            binding.otpLayout.main.visibility = View.VISIBLE
            binding.personalInformationLayout.main.visibility = View.GONE
            binding.otpLayout.verify.isEnabled = false
            otp1 = otpWatcher(this, binding.otpLayout.otpView, binding.otpLayout.verify)

            timer()
        }

        binding.otpLayout.verify.setOnClickListener {
            PersistentUser.getInstance().setLogin(this)
            hideKeyboard(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun timer() {
        binding.otpLayout.resend.isClickable = false
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                " ${millisUntilFinished / 1000}s".also { binding.otpLayout.resend.text = it }
            }

            override fun onFinish() {
                binding.otpLayout.resend.text = getString(R.string.resend_code)
                binding.otpLayout.resend.isClickable = true
            }
        }
        timer?.start()
    }
}