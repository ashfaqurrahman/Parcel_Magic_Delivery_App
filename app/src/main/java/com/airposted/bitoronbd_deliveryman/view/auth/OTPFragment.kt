package com.airposted.bitoronbd_deliveryman.view.auth

import `in`.aabhasjindal.otptextview.OTPListener
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.MalformedJsonException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentOTPBinding
import com.airposted.bitoronbd_deliveryman.model.register.SignUpModel
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.MainActivity
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File

class OTPFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentOTPBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var phone: String? = null
    var otp1: String? = null
    var otp: String? = null
    private var timer: CountDownTimer? = null
    private var isAuth = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        otp = requireArguments().getString("otp")
        isAuth = requireArguments().getBoolean("isAuth")
        binding.toolbar.toolbarTitle.text = getString(R.string.verification)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        phone = requireArguments().getString("phone")
        binding.otpTopText.text = getString(R.string.enter) + phone
        communicatorFragmentInterface = context as AuthCommunicatorFragmentInterface

        timer()

        binding.verify.isEnabled = false
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.verify.isEnabled = false
                binding.verify.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.before_button_bg
                )
                binding.otpView.resetState()
            }

            override fun onOTPComplete(otp: String) {
                otp1 = otp
                binding.verify.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.after_button_bg
                )
                binding.verify.isEnabled = true
            }
        }

        binding.verify.setOnClickListener {
            verifyOTP()
        }

        binding.resend.setOnClickListener {
            resendOTP()
        }
    }

    private fun verifyOTP() {
        if (otp1 == otp) {
            timer?.cancel()
            if (isAuth) {
                dismissDialog()
                hideKeyboard(requireActivity())
                Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                PersistentUser.getInstance().setLogin(requireContext())
                PersistentUser.getInstance().setAccessToken(requireContext(), "Bearer " + requireArguments().getString("token"))
                PersistentUser.getInstance().setUserID(
                    requireContext(),
                    requireArguments().getInt("id").toString()
                )
                PersistentUser.getInstance().setFullname(requireContext(), requireArguments().getString("name"))
                PersistentUser.getInstance().setPhonenumber(requireContext(), requireArguments().getString("phone"))
                PersistentUser.getInstance().setUserImage(requireContext(), requireArguments().getString("image"))
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else {
                dismissDialog()
                hideKeyboard(requireActivity())
                lifecycleScope.launch {
                    try {

                        val imagePart: MultipartBody.Part?
                        val imagePhotoName: RequestBody?
                        val imagePath = requireArguments().getString("imageUri")
                        val imageFile = File(imagePath!!)
                        val compressedImage = reduceImageSize(imageFile)

                        val idPart: MultipartBody.Part?
                        val idPhotoName: RequestBody?
                        val idPath = requireArguments().getString("idUri")
                        val idFile = File(idPath!!)
                        val compressedId = reduceImageSize(idFile)

                        if (compressedImage != null && compressedId != null) {
                            val imageFileReqBody = RequestBody.create(
                                MediaType.parse("image/*"),
                                compressedImage
                            )
                            imagePart = MultipartBody.Part.createFormData(
                                "image",
                                compressedImage.name,
                                imageFileReqBody
                            )
                            imagePhotoName = RequestBody.create(
                                MediaType.parse("text/plain"),
                                "image-type"
                            )
                            val idFileReqBody = RequestBody.create(
                                MediaType.parse("image/*"),
                                compressedId
                            )
                            idPart = MultipartBody.Part.createFormData(
                                "image",
                                compressedId.name,
                                idFileReqBody
                            )
                            idPhotoName = RequestBody.create(
                                MediaType.parse("text/plain"),
                                "image-type"
                            )
                        } else {
                            val imageFileReqBody = RequestBody.create(
                                MediaType.parse("image/*"),
                                imageFile
                            )
                            imagePart = MultipartBody.Part.createFormData(
                                "image",
                                imageFile.name,
                                imageFileReqBody
                            )
                            imagePhotoName = RequestBody.create(
                                MediaType.parse("text/plain"),
                                "image-type"
                            )
                            val idFileReqBody = RequestBody.create(
                                MediaType.parse("image/*"),
                                imageFile
                            )
                            idPart = MultipartBody.Part.createFormData(
                                "image",
                                imageFile.name,
                                idFileReqBody
                            )
                            idPhotoName = RequestBody.create(
                                MediaType.parse("text/plain"),
                                "image-type"
                            )

                        }

                        val name = RequestBody.create(
                            MediaType.parse("text/plain"),
                            requireArguments().getString("name")!!
                        )
                        val phone = RequestBody.create(
                            MediaType.parse("text/plain"),
                            requireArguments().getString("phone")!!
                        )
                        val dob = RequestBody.create(
                            MediaType.parse("text/plain"),
                            requireArguments().getString("dob")!!
                        )
                        val gender = RequestBody.create(
                            MediaType.parse("text/plain"),
                            requireArguments().getInt("gender").toString()
                        )
                        val address = RequestBody.create(
                            MediaType.parse("text/plain"),
                            requireArguments().getString("address")!!
                        )
                        if (requireArguments().getString("idType") == "National ID"){
                            val signUpResponse = viewModel.userSignUpWithNid(
                                name,
                                phone,
                                idPart,
                                idPhotoName,
                                dob,
                                gender,
                                address,
                                imagePart,
                                imagePhotoName
                            )

                            if (signUpResponse.data != null) {
                                dismissDialog()
                                binding.main.snackbar(signUpResponse.msg)
                                createAccountSuccessDialog()
                            } else {
                                dismissDialog()
                                binding.main.snackbar(signUpResponse.msg)
                            }
                        } else if (requireArguments().getString("idType") == "Driving Lic.") {
                            val signUpResponse = viewModel.userSignUpWithDriveLicense(
                                name,
                                phone,
                                idPart,
                                idPhotoName,
                                dob,
                                gender,
                                address,
                                imagePart,
                                imagePhotoName
                            )

                            if (signUpResponse.data != null) {
                                dismissDialog()
                                binding.main.snackbar(signUpResponse.msg)
                                createAccountSuccessDialog()
                            } else {
                                dismissDialog()
                                binding.main.snackbar(signUpResponse.msg)
                            }
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
        }
        else {
            binding.main.snackbar("Incorrect OTP")
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
            requireActivity().supportFragmentManager.popBackStack(PhoneNumberFragment::class.java.name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            communicatorFragmentInterface?.addContentFragment(PhoneNumberFragment(), false)
        }
        dialogs.setCancelable(false)
        dialogs.show()
    }

    private fun timer() {
        binding.resend.isClickable = false
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                "${millisUntilFinished / 1000}s".also { binding.resend.text = it }
            }

            override fun onFinish() {
                binding.resend.text = getString(R.string.resend_code)
                binding.resend.isClickable = true
            }
        }
        timer?.start()
    }

    private fun resendOTP() {
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.sendOTP(
                    requireArguments().getString("phone")!!
                )
                if (response.success) {
                    timer()
                    otp = response.data?.token
                } else {
                    binding.main.snackbar(response.msg)
                }
                dismissDialog()
            } catch (e: JsonSyntaxException) {
                dismissDialog()
                binding.main.snackbar(e.message!!)
                e.printStackTrace()
            } catch (e: MalformedJsonException) {
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

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}