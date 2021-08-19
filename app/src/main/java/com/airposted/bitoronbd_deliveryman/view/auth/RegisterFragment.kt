package com.airposted.bitoronbd_deliveryman.view.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.MalformedJsonException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentRegisterBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import com.google.gson.JsonSyntaxException
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class RegisterFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: FragmentRegisterBinding
    private var communicatorFragmentInterface: AuthCommunicatorFragmentInterface? = null
    private var gender: String? = null
    private var flag: Int? = null
    private var idType: String? = null
    private var driverType: Int? = null
    private var mCropImageUri: Uri? = null
    private var mCropIDUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        customTextView(binding.tvTermsConditionSignUp, requireContext())
        communicatorFragmentInterface = context as AuthCommunicatorFragmentInterface
        binding.toolbar.toolbarTitle.text = getString(R.string.sign_up_title)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.imageUpload.setOnClickListener {
            flag = 0
            uploadImage()
        }

        binding.id.setOnClickListener {
            flag = 1
            uploadImage()
        }

        binding.gender.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            gender = newText
        }

        binding.idType.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            idType = newText
            binding.id.visibility = View.VISIBLE
        }

        binding.driverType.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
            driverType = newIndex + 1
            binding.id.visibility = View.VISIBLE
        }

        binding.signUp.setOnClickListener {
            hideKeyboard(requireActivity())
            val name = binding.name.text.toString()
            if (mCropImageUri != null && mCropIDUri != null) {
                if (name.isNotEmpty()) {
                    sendOTP()
                } else {
                    binding.main.snackbar("Username should not empty")
                }
            } else {
                binding.main.snackbar("User photo & ID card is required")
            }
        }
    }

    private fun sendOTP() {
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.sendOTP(
                    requireArguments().getString("phone")!!
                )
                if (response.success) {
                    val fragment = OTPFragment()
                    val bundle = Bundle()
                    bundle.putString("otp", response.data?.token)
                    bundle.putString("imageUri", mCropImageUri?.path)
                    bundle.putString("idUri", mCropIDUri?.path)
                    bundle.putString("idType", idType)
                    bundle.putInt("driverType", driverType!!)
                    bundle.putString("phone", requireArguments().getString("phone"))
                    bundle.putString("name", binding.name.text.toString())
                    bundle.putString("address", binding.address.text.toString())
//                    if (id == "National ID") {
//                        bundle.putString("nid", binding.idNumber.text.toString())
//                    } else {
//                        bundle.putString("drive_lisence", binding.idNumber.text.toString())
//                    }
                    if (gender == "Male") {
                        bundle.putInt("gender", 1)
                    } else {
                        bundle.putInt("gender", 0)
                    }
                    bundle.putString("dob", binding.name.text.toString())
                    bundle.putBoolean("isAuth", false)
                    fragment.arguments = bundle
                    communicatorFragmentInterface?.addContentFragment(fragment, true)
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

    private fun uploadImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                imagePick()
            }
        } else {
            imagePick()
        }
    }

    private fun imagePick() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
//            .setAspectRatio(1, 1)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                when (flag) {
                    0 -> {
                        mCropImageUri = result.uri
                        binding.profileImage.setImageURI(mCropImageUri)
                    }
                    1 -> {
                        mCropIDUri = result.uri
                        binding.id.setImageURI(mCropIDUri)
                    }
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}