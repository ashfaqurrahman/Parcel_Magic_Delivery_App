package com.airposted.bitoronbd_deliveryman.view.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.airposted.bitoronbd_deliveryman.data.repositories.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody


class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    suspend fun checkNumber(
        mobile: String
    ) = withContext(Dispatchers.IO) { repository.numberCheck(mobile) }

//    suspend fun getLocations(
//        mobile: String
//    ) = withContext(Dispatchers.IO) { repository.locationSearch(mobile) }
//
//    /*suspend fun userLogin(
//        email: String,
//        password: String
//    ) = withContext(Dispatchers.IO) { repository.userLogin(email, password) }*/

    suspend fun userSignUp(
        name: String,
        phone: String,
        password: String,
        driveLicence: String,
        dob: String,
        gender: String,
        address: String
    ) = withContext(Dispatchers.IO) { repository.userSignUp(name, phone, password, driveLicence, dob, gender, address) }

    suspend fun userSignUpWithPhoto(
        name: RequestBody,
        phone: RequestBody,
        drivingLicence: RequestBody,
        dob: RequestBody,
        gender: RequestBody,
        address: RequestBody,
        photo: MultipartBody.Part,
        photo_name: RequestBody
    ) = withContext(Dispatchers.IO) { repository.userSignUpWithPhoto(name, phone, drivingLicence, dob, gender, address, photo, photo_name) }

}