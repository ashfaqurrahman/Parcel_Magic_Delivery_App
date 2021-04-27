package com.airposted.bitoronbd_deliveryman.view.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.data.network.responses.AuthResponse
import com.airposted.bitoronbd_deliveryman.databinding.FragmentOTPBinding
import com.airposted.bitoronbd_deliveryman.utils.dismissDialog
import com.airposted.bitoronbd_deliveryman.utils.otpWatcher
import com.airposted.bitoronbd_deliveryman.utils.setProgressDialog
import com.airposted.bitoronbd_deliveryman.utils.snackbar
import com.airposted.bitoronbd_deliveryman.view.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class OTPFragment : Fragment() {
    private lateinit var binding: FragmentOTPBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var phone: String? = null
    var otp1: String? = null
    private var timer: CountDownTimer? = null
    private lateinit var mAuth: FirebaseAuth
    private var verificationId: String? = null
    private var isAuth = false
    private var authResponse: AuthResponse? = null
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
        mAuth = Firebase.auth
        isAuth = requireArguments().getBoolean("isAuth")
        binding.toolbar.toolbarTitle.text = getString(R.string.verification)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        phone = requireArguments().getString("phone")
        binding.otpTopText.text = getString(R.string.enter, phone)
        communicatorFragmentInterface = context as AuthCommunicatorFragmentInterface
        phone = requireArguments().getString("phone")
        otp1 = otpWatcher(requireContext(), binding.otpView, binding.verify)

        timer()
        sendVerificationCode(phone!!)

        binding.verify.setOnClickListener {

        }

        binding.resend.setOnClickListener {
            timer()
            sendVerificationCode(phone!!)
        }
    }

    private fun sendVerificationCode(number: String) {

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    binding.otpView.setOTP(code)
                    setProgressDialog(requireContext())
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    signInWithCredential(credential)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dismissDialog()
                binding.main.snackbar(e.message!!)
            }
        }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (isAuth){
                        dismissDialog()
                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                        //binding.rootLayout.snackbar("Login Successful")
                        PersistentUser.getInstance().setLogin(requireContext())
                        PersistentUser.getInstance().setAccessToken(requireContext(), "Bearer " + authResponse?.data?.token)
                        PersistentUser.getInstance().setUserID(
                            requireContext(),
                            authResponse?.user?.id.toString()
                        )
                        PersistentUser.getInstance().setFullname(requireContext(), authResponse?.user?.name)
                        PersistentUser.getInstance().setPhonenumber(requireContext(), authResponse?.user?.phone)
                        PersistentUser.getInstance().setUserImage(requireContext(), authResponse?.user?.image)
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    } else {
//                        lifecycleScope.launch {
//                            try {
//                                val signupResponse: AuthResponse?
//                                if (cropImageUri != null){
//                                    val file = File(cropImageUri?.path)
//                                    val compressedImage = reduceImageSize(file)
//                                    if (compressedImage != null){
//                                        val fileReqBody = RequestBody.create(
//                                            MediaType.parse("image/*"),
//                                            compressedImage
//                                        )
//                                        val part = MultipartBody.Part.createFormData(
//                                            "image",
//                                            compressedImage.name,
//                                            fileReqBody
//                                        )
//                                        val photoName = RequestBody.create(
//                                            MediaType.parse("text/plain"),
//                                            "image-type"
//                                        )
//                                        val name = RequestBody.create(
//                                            MediaType.parse("text/plain"),
//                                            binding.signUpLayout.name.text.toString()
//                                        )
//                                        val phone = RequestBody.create(
//                                            MediaType.parse("text/plain"),
//                                            "+880$phone"
//                                        )
//                                        signupResponse = viewModel.userSignupWithPhoto(
//                                            name,
//                                            phone,
//                                            part,
//                                            photoName
//                                        )
//                                    } else {
//                                        val fileReqBody = RequestBody.create(
//                                            MediaType.parse("image/*"),
//                                            file
//                                        )
//                                        val part = MultipartBody.Part.createFormData(
//                                            "image",
//                                            file.name,
//                                            fileReqBody
//                                        )
//                                        val photoName = RequestBody.create(
//                                            MediaType.parse("text/plain"),
//                                            "image-type"
//                                        )
//                                        val name = RequestBody.create(
//                                            MediaType.parse("text/plain"),
//                                            binding.signUpLayout.name.text.toString()
//                                        )
//                                        val phone = RequestBody.create(
//                                            MediaType.parse("text/plain"),
//                                            "+880$phone"
//                                        )
//                                        signupResponse = viewModel.userSignupWithPhoto(
//                                            name,
//                                            phone,
//                                            part,
//                                            photoName
//                                        )
//                                    }
//
//                                } else {
//                                    signupResponse = viewModel.userSignup(
//                                        binding.signUpLayout.name.text.toString(),
//                                        "+880$phone"
//                                    )
//                                }
//                                if (signupResponse.success) {
//                                    dismissDialog()
//                                    toast(signupResponse.msg)
//                                    PersistentUser.getInstance().setLogin(this@SignInSignUpActivity)
//                                    PersistentUser.getInstance().setAccessToken(
//                                        this@SignInSignUpActivity,
//                                        "Bearer " + signupResponse.data?.token
//                                    )
//                                    PersistentUser.getInstance().setUserID(
//                                        this@SignInSignUpActivity,
//                                        signupResponse.user?.id.toString()
//                                    )
//                                    PersistentUser.getInstance().setFullname(
//                                        this@SignInSignUpActivity,
//                                        signupResponse.user?.name
//                                    )
//                                    PersistentUser.getInstance().setPhonenumber(
//                                        this@SignInSignUpActivity,
//                                        signupResponse.user?.phone
//                                    )
//                                    PersistentUser.getInstance().setUserImage(
//                                        this@SignInSignUpActivity,
//                                        signupResponse.user?.image
//                                    )
//
//                                    val intent = Intent(applicationContext, PermissionActivity::class.java)
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                                    startActivity(intent)
//                                    finish()
//
//                                } else {
//                                    dismissDialog()
//                                    binding.rootLayout.snackbar(signupResponse.msg)
//                                }
//
//                            } catch (e: ApiException) {
//                                dismissDialog()
//                                binding.rootLayout.snackbar(e.message!!)
//                                e.printStackTrace()
//                            } catch (e: NoInternetException) {
//                                dismissDialog()
//                                binding.rootLayout.snackbar(e.message!!)
//                                e.printStackTrace()
//                            }
//                        }
                    }

                } else {
                    binding.main.snackbar(task.exception!!.message!!)
                }
            }
    }

    private fun timer() {
        binding.resend.isClickable = false
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                " ${millisUntilFinished / 1000}s".also { binding.resend.text = it }
            }

            override fun onFinish() {
                binding.resend.text = getString(R.string.resend_code)
                binding.resend.isClickable = true
            }
        }
        timer?.start()
    }
}