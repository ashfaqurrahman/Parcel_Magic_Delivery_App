package com.airposted.bitoronbd_deliveryman.data.repositories

import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.SafeApiRequest
import com.airposted.bitoronbd_deliveryman.data.network.responses.AuthResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(
    private val api: MyApi,
    //private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun numberCheck(mobile: String): AuthResponse {
        return apiRequest { api.numberCheck(mobile) }
    }

//    suspend fun locationSearch(mobile: String): SearchLocation {
//        return apiRequest { api.getPlacesNameList(mobile) }
//    }

    /*suspend fun userLogin(email: String, password: String): AuthResponse {
        return apiRequest { api.userLogin(email, password) }
    }*/

    suspend fun userSignUp(
        name: String,
        phone: String,
        password: String,
        driveLicence: String,
        dob: String,
        gender: String,
        address: String
    ) : AuthResponse {
        return apiRequest{ api.userSignUp(name, phone, password, driveLicence, dob, gender, address)}
    }

    suspend fun userSignUpWithPhoto(
        name: RequestBody,
        phone: RequestBody,
        password: RequestBody,
        drivingLicence: RequestBody,
        dob: RequestBody,
        gender: RequestBody,
        address: RequestBody,
        photo: MultipartBody.Part,
        photo_name: RequestBody
    ) : AuthResponse {
        return apiRequest{ api.userSignUpWithPhoto(name, phone, password, drivingLicence, dob, gender, address, photo, photo_name)}
    }

    /*suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getuser()*/

}