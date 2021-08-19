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

    suspend fun sendOTP(
        mobile: String
    ) = withContext(Dispatchers.IO) { repository.sendOTP(mobile) }

//    suspend fun getLocations(
//        mobile: String
//    ) = withContext(Dispatchers.IO) { repository.locationSearch(mobile) }
//
//    /*suspend fun userLogin(
//        email: String,
//        password: String
//    ) = withContext(Dispatchers.IO) { repository.userLogin(email, password) }*/

    suspend fun userSignUpWithNid(
        name: RequestBody,
        phone: RequestBody,
        driverType: Int,
        nid: MultipartBody.Part,
        nidName: RequestBody,
        dob: RequestBody,
        gender: RequestBody,
        address: RequestBody,
        photo: MultipartBody.Part,
        photo_name: RequestBody
    ) = withContext(Dispatchers.IO) { repository.userSignUpWithNid(name, phone, driverType, nid, nidName, dob, gender, address, photo, photo_name) }

    suspend fun userSignUpWithDriveLicense(
        name: RequestBody,
        phone: RequestBody,
        driverType: Int,
        drivingLicence: MultipartBody.Part,
        drivingLicenceName: RequestBody,
        dob: RequestBody,
        gender: RequestBody,
        address: RequestBody,
        photo: MultipartBody.Part,
        photo_name: RequestBody
    ) = withContext(Dispatchers.IO) { repository.userSignUpWithDriveLicense(name, phone, driverType, drivingLicence, drivingLicenceName, dob, gender, address, photo, photo_name) }

}