package com.airposted.bitoronbd_deliveryman.data.repositories

import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.SafeApiRequest
import com.airposted.bitoronbd_deliveryman.model.auth.AuthResponse
import com.airposted.bitoronbd_deliveryman.model.register.SignUpModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(
    private val api: MyApi,
    //private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun numberCheck(mobile: String): AuthResponse {
        return apiRequest { api.numberCheck(mobile) }
    }

    suspend fun sendOTP(mobile: String): AuthResponse {
        return apiRequest { api.sendOTP(mobile) }
    }

//    suspend fun locationSearch(mobile: String): SearchLocation {
//        return apiRequest { api.getPlacesNameList(mobile) }
//    }

    /*suspend fun userLogin(email: String, password: String): AuthResponse {
        return apiRequest { api.userLogin(email, password) }
    }*/

    suspend fun userSignUpWithNid(
        name: RequestBody,
        phone: RequestBody,
        driverType: Int,
        nid: MultipartBody.Part,
        nid_name: RequestBody,
        dob: RequestBody,
        gender: RequestBody,
        address: RequestBody,
        photo: MultipartBody.Part,
        photo_name: RequestBody
    ) : SignUpModel {
        return apiRequest{ api.userSignUpWithNid(name, phone, driverType, nid, nid_name, dob, gender, address, photo,  photo_name)}
    }

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
    ) : SignUpModel {
        return apiRequest{ api.userSignUpWithDriveLicense(name, phone, driverType, drivingLicence, drivingLicenceName, dob, gender, address, photo, photo_name)}
    }

    /*suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getuser()*/

}