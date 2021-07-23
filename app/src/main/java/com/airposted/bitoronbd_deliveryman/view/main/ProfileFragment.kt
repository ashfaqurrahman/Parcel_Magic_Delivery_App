package com.airposted.bitoronbd_deliveryman.view.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.MalformedJsonException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentProfileBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File

class ProfileFragment : Fragment(), KodeinAware, IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentProfileBinding
    var edit = false
    private var cropImageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.name.observe(viewLifecycleOwner) {
            binding.profileName.text = it
        }

        viewModel.image.observe(viewLifecycleOwner) {
            Glide.with(requireActivity()).load(
                it
            ).placeholder(R.mipmap.ic_launcher).error(
                R.drawable.sample_pro_pic
            ).into(binding.profileImage)
        }

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getAverageRating(PersistentUser.getInstance().getUserID(requireContext()).toInt())
                binding.ratingBar.rating = response.rating[0].ratings_average.toFloat()
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
            dismissDialog()
        }

        if (PersistentUser.getInstance().getFullName(requireActivity()).isEmpty()) {
            binding.profileName.text = "No Name"
        } else {
            binding.profileName.text = PersistentUser.getInstance().getFullName(requireActivity())
        }

        binding.phone.text = PersistentUser.getInstance().getPhoneNumber(requireActivity())

        binding.editName.setOnClickListener {
            if (!edit) {
                binding.editProfileName.setText(
                    PersistentUser.getInstance().getFullName(requireActivity())
                )
                binding.profileName.visibility = View.GONE
                binding.editProfileName.visibility = View.VISIBLE
                binding.editProfileName.requestFocus()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.editProfileName, InputMethodManager.SHOW_IMPLICIT)
                binding.editName.setImageResource(R.drawable.ic_done)
                edit = true
            } else {

                hideKeyboard(requireActivity())
                if (binding.editProfileName.text.toString() != "" && binding.editProfileName.text.toString() != PersistentUser.getInstance()
                        .getFullName(requireActivity())
                ) {
                    setProgressDialog(requireActivity())
                    val name = binding.editProfileName.text.toString().trimEnd()
                    lifecycleScope.launch {
                        try {
                            val response = viewModel.userNameUpdate(name)
                            dismissDialog()
                            binding.profileName.text = name
                            PersistentUser.getInstance().setFullname(
                                requireContext(),
                                name
                            )
                            binding.profileName.visibility = View.VISIBLE
                            binding.editProfileName.visibility = View.GONE
                            binding.editName.setImageResource(R.drawable.ic_edit)
                            edit = false
                            binding.rootLayout.snackbar(response.msg)
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
                } else {
                    binding.profileName.visibility = View.VISIBLE
                    binding.editProfileName.visibility = View.GONE
                    binding.editName.setImageResource(R.drawable.ic_edit)
                    edit = false
                }
            }
        }

        binding.imageUpload.setOnClickListener {
            uploadImage()
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
            .setAspectRatio(1, 1)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                cropImageUri = result.uri
                setProgressDialog(requireActivity())
                lifecycleScope.launch {
                    try {
                        val file = File(cropImageUri!!.path)
                        val compressedImage = reduceImageSize(file)
                        val fileReqBody = RequestBody.create(
                            MediaType.parse("image/*"),
                            compressedImage!!
                        )
                        val part = MultipartBody.Part.createFormData(
                            "image",
                            compressedImage.name,
                            fileReqBody
                        )
                        val photoName = RequestBody.create(
                            MediaType.parse("text/plain"),
                            "image-type"
                        )
                        val response = viewModel.userImageUpdate(
                            PersistentUser.getInstance().getAccessToken(
                                requireActivity()
                            ), part, photoName
                        )
                        if (response.success) {
                            PersistentUser.getInstance().setUserImage(
                                requireActivity(),
                                response.data.image
                            )
                            binding.profileImage.setImageURI(cropImageUri)
                            PersistentUser.getInstance()
                                .setUserImage(requireContext(), cropImageUri!!.path.toString())
                            dismissDialog()
                            binding.rootLayout.snackbar(response.msg)
                        } else {
                            binding.rootLayout.snackbar("Update failed")
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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                binding.rootLayout.snackbar(error.toString())
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePick()
            } else {

            }
        }
    }
}