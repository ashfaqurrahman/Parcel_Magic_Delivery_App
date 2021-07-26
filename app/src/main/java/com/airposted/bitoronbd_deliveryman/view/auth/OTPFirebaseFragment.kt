package com.airposted.bitoronbd_deliveryman.view.auth

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airposted.bitoronbd_deliveryman.databinding.FragmentOTPFirebaseBinding
import com.airposted.bitoronbd_deliveryman.model.auth.AuthResponse
import com.airposted.bitoronbd_deliveryman.utils.*
import com.google.firebase.auth.FirebaseAuth
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class OTPFirebaseFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentOTPFirebaseBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var phone: String? = null
    var otp1: String? = null
    private var timer: CountDownTimer? = null
    private lateinit var mAuth: FirebaseAuth
    private var verificationId: String? = null
    private var isAuth = false
    private var authResponse: AuthResponse? = null
    private var nid: RequestBody? = null
    private var gander: RequestBody? = null
    private var drivingLicence: RequestBody? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPFirebaseBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        bindUI()
    }

//    private fun bindUI() {
//        mAuth = FirebaseAuth.getInstance()
//        isAuth = requireArguments().getBoolean("isAuth")
//        binding.toolbar.toolbarTitle.text = getString(R.string.verification)
//        binding.toolbar.backImage.setOnClickListener {
//            requireActivity().onBackPressed()
//        }
//        phone = requireArguments().getString("phone")
//        binding.otpTopText.text = getString(R.string.enter) + phone
//        communicatorFragmentInterface = context as AuthCommunicatorFragmentInterface
//
//        timer()
//        sendVerificationCode(phone!!)
//
//        binding.verify.isEnabled = false
//        binding.otpView.otpListener = object : OTPListener {
//            override fun onInteractionListener() {
//                binding.verify.isEnabled = false
//                binding.verify.background = ContextCompat.getDrawable(
//                    requireActivity(),
//                    R.drawable.before_button_bg
//                )
//                binding.otpView.resetState()
//            }
//
//            override fun onOTPComplete(otp: String) {
//                otp1 = otp
//                binding.verify.background = ContextCompat.getDrawable(
//                    requireActivity(),
//                    R.drawable.after_button_bg
//                )
//                binding.verify.isEnabled = true
//            }
//        }
//
//        binding.verify.setOnClickListener {
//            setProgressDialog(requireContext())
//            val code = otp1
//            val credential = PhoneAuthProvider.getCredential(verificationId!!, code!!)
//            signInWithCredential(credential)
//        }
//
//        binding.resend.setOnClickListener {
//            timer()
//            sendVerificationCode(phone!!)
//        }
//    }

//    private fun sendVerificationCode(number: String) {
//
//        val options = PhoneAuthOptions.newBuilder(mAuth)
//            .setPhoneNumber(number)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(requireActivity())
//            .setCallbacks(mCallBack)
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
//
//    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
//        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            override fun onCodeSent(
//                s: String,
//                forceResendingToken: PhoneAuthProvider.ForceResendingToken
//            ) {
//                super.onCodeSent(s, forceResendingToken)
//                verificationId = s
//            }
//
//            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
//                val code = phoneAuthCredential.smsCode
//                if (code != null) {
//                    binding.otpView.setOTP(code)
//                    setProgressDialog(requireContext())
//                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
//                    signInWithCredential(credential)
//                }
//            }
//
//            override fun onVerificationFailed(e: FirebaseException) {
//                dismissDialog()
//                binding.main.snackbar(e.message!!)
//            }
//        }
//
//    private fun signInWithCredential(credential: PhoneAuthCredential) {
//        mAuth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//
//                    timer?.cancel()
//                    if (isAuth) {
//                        dismissDialog()
//                        hideKeyboard(requireActivity())
//                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
//                        PersistentUser.getInstance().setLogin(requireContext())
//                        PersistentUser.getInstance().setAccessToken(requireContext(), "Bearer " + requireArguments().getString("token"))
//                        PersistentUser.getInstance().setUserID(
//                            requireContext(),
//                            requireArguments().getInt("id").toString()
//                        )
//                        PersistentUser.getInstance().setFullname(requireContext(), requireArguments().getString("name"))
//                        PersistentUser.getInstance().setPhonenumber(requireContext(), requireArguments().getString("phone"))
//                        PersistentUser.getInstance().setUserImage(requireContext(), requireArguments().getString("image"))
//                        val intent = Intent(requireContext(), MainActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                        startActivity(intent)
//                    }
//                    else {
//                        dismissDialog()
//                        hideKeyboard(requireActivity())
//                        lifecycleScope.launch {
//                            try {
//                                var signUpResponse: AuthResponse?
//                                val part: MultipartBody.Part?
//                                val photoName: RequestBody?
//                                val path = requireArguments().getString("imageUri")
//                                val file = File(path!!)
//                                val compressedImage = reduceImageSize(file)
//                                if (compressedImage != null) {
//                                    val fileReqBody = RequestBody.create(
//                                        MediaType.parse("image/*"),
//                                        compressedImage
//                                    )
//                                    part = MultipartBody.Part.createFormData(
//                                        "image",
//                                        compressedImage.name,
//                                        fileReqBody
//                                    )
//                                    photoName = RequestBody.create(
//                                        MediaType.parse("text/plain"),
//                                        "image-type"
//                                    )
//                                } else {
//                                    val fileReqBody = RequestBody.create(
//                                        MediaType.parse("image/*"),
//                                        file
//                                    )
//                                    part = MultipartBody.Part.createFormData(
//                                        "image",
//                                        file.name,
//                                        fileReqBody
//                                    )
//                                    photoName = RequestBody.create(
//                                        MediaType.parse("text/plain"),
//                                        "image-type"
//                                    )
//
//                                }
//
//                                val name = RequestBody.create(
//                                    MediaType.parse("text/plain"),
//                                    requireArguments().getString("name")!!
//                                )
//                                val phone = RequestBody.create(
//                                    MediaType.parse("text/plain"),
//                                    requireArguments().getString("phone")!!
//                                )
//                                val dob = RequestBody.create(
//                                    MediaType.parse("text/plain"),
//                                    requireArguments().getString("dob")!!
//                                )
//                                val gender = RequestBody.create(
//                                    MediaType.parse("text/plain"),
//                                    requireArguments().getInt("gender").toString()
//                                )
//                                val address = RequestBody.create(
//                                    MediaType.parse("text/plain"),
//                                    requireArguments().getString("address")!!
//                                )
//                                if (requireArguments().getString("nid") != null){
//                                    nid = RequestBody.create(
//                                        MediaType.parse("text/plain"),
//                                        requireArguments().getString("nid")!!
//                                    )
//
//                                    signUpResponse = viewModel.userSignUpWithNid(
//                                        name,
//                                        phone,
//                                        part,
//                                        photoName,
//                                        dob,
//                                        gender,
//                                        address,
//                                        part,
//                                        photoName
//                                    )
//
//                                    if (signUpResponse.data != null) {
//                                        dismissDialog()
//                                        binding.main.snackbar(signUpResponse.msg)
//                                        /*PersistentUser.getInstance().setLogin(requireContext())
//                                        PersistentUser.getInstance().setAccessToken(
//                                            requireContext(),
//                                            "Bearer " + signUpResponse.data?.token
//                                        )
//                                        PersistentUser.getInstance().setUserID(
//                                            requireContext(),
//                                            signUpResponse.user?.id.toString()
//                                        )
//                                        PersistentUser.getInstance().setFullname(
//                                            requireContext(),
//                                            signUpResponse.user?.username
//                                        )
//                                        PersistentUser.getInstance().setPhonenumber(
//                                            requireContext(),
//                                            signUpResponse.user?.phone
//                                        )
//                                        PersistentUser.getInstance().setUserImage(
//                                            requireContext(),
//                                            signUpResponse.user?.image
//                                        )*/
//                                        createAccountSuccessDialog()
//                                    } else {
//                                        dismissDialog()
//                                        binding.main.snackbar(signUpResponse.msg)
//                                    }
//                                }
//
//                                if (requireArguments().getString("drive_lisence") != null){
//                                    drivingLicence = RequestBody.create(
//                                        MediaType.parse("text/plain"),
//                                        requireArguments().getString("drive_lisence")!!
//                                    )
//
//                                    signUpResponse = viewModel.userSignUpWithDriveLicense(
//                                        name,
//                                        phone,
//                                        part,
//                                        photoName,
//                                        dob,
//                                        gender,
//                                        address,
//                                        part,
//                                        photoName
//                                    )
//
//                                    if (signUpResponse.data != null) {
//                                        dismissDialog()
//                                        /*binding.main.snackbar(signUpResponse.msg)
//                                        PersistentUser.getInstance().setLogin(requireContext())
//                                        PersistentUser.getInstance().setAccessToken(
//                                            requireContext(),
//                                            "Bearer " + signUpResponse.data?.token
//                                        )
//                                        PersistentUser.getInstance().setUserID(
//                                            requireContext(),
//                                            signUpResponse.user?.id.toString()
//                                        )
//                                        PersistentUser.getInstance().setFullname(
//                                            requireContext(),
//                                            signUpResponse.user?.username
//                                        )
//                                        PersistentUser.getInstance().setPhonenumber(
//                                            requireContext(),
//                                            signUpResponse.user?.phone
//                                        )
//                                        PersistentUser.getInstance().setUserImage(
//                                            requireContext(),
//                                            signUpResponse.user?.image
//                                        )*/
//                                        createAccountSuccessDialog()
//                                    } else {
//                                        dismissDialog()
//                                        binding.main.snackbar(signUpResponse.msg)
//                                    }
//                                }
//
//                            } catch (e: ApiException) {
//                                dismissDialog()
//                                binding.main.snackbar(e.message!!)
//                                e.printStackTrace()
//                            } catch (e: NoInternetException) {
//                                dismissDialog()
//                                binding.main.snackbar(e.message!!)
//                                e.printStackTrace()
//                            }
//                        }
//                    }
//
//                } else {
//                    binding.main.snackbar(task.exception!!.message!!)
//                }
//            }
//    }
//
//    private fun createAccountSuccessDialog() {
//        val dialogs = Dialog(requireActivity())
//        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialogs.setContentView(R.layout.create_account_dialog)
//        dialogs.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialogs.window?.setLayout(
//            ViewGroup.LayoutParams.WRAP_CONTENT,  //w
//            ViewGroup.LayoutParams.MATCH_PARENT //h
//        )
//        val done = dialogs.findViewById<TextView>(R.id.done)
//        done.setOnClickListener {
//            dialogs.dismiss()
//            requireActivity().supportFragmentManager.popBackStack(PhoneNumberFragment::class.java.name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            communicatorFragmentInterface?.addContentFragment(PhoneNumberFragment(), false)
//        }
//        dialogs.setCancelable(false)
//        dialogs.show()
//    }
//
//    private fun timer() {
//        binding.resend.isClickable = false
//        timer = object : CountDownTimer(60000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                " ${millisUntilFinished / 1000}s".also { binding.resend.text = it }
//            }
//
//            override fun onFinish() {
//                binding.resend.text = getString(R.string.resend_code)
//                binding.resend.isClickable = true
//            }
//        }
//        timer?.start()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        timer?.cancel()
//    }
}