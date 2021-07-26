package com.airposted.bitoronbd_deliveryman.view.main.home

import androidx.lifecycle.ViewModel
import com.airposted.bitoronbd_deliveryman.data.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    suspend fun getAllAreaList() = withContext(Dispatchers.IO) { repository.getAllAreaList() }

    suspend fun addMyArea(areaId: Int) = withContext(Dispatchers.IO) { repository.addMyArea(areaId) }

    suspend fun viewMyArea() = withContext(Dispatchers.IO) { repository.viewMyArea() }

    suspend fun deleteMyArea(id: Int) = withContext(Dispatchers.IO) { repository.deleteMyArea(id) }

    suspend fun myProfile() = withContext(Dispatchers.IO) { repository.myProfile() }

    suspend fun getAverageRating() = withContext(Dispatchers.IO) { repository.getAverageRating() }

    val name = repository.userName
    val image = repository.userImage

    suspend fun myWallet() = withContext(Dispatchers.IO) { repository.myWallet() }

    suspend fun getOrderList(to: Int, from: Int) = withContext(Dispatchers.IO) { repository.getOrderList(to, from) }

    suspend fun getOrderListByArea(to: Int) = withContext(Dispatchers.IO) { repository.getOrderListByArea(to) }

    suspend fun getOrderDetails(id: String) = withContext(Dispatchers.IO) { repository.getOrderDetails(id) }

    suspend fun changeStatus(invoice: String, status: Int, otp: Int) = withContext(Dispatchers.IO) { repository.changeStatus(invoice, status, otp) }

    suspend fun myOrderHistory() = withContext(Dispatchers.IO) { repository.myOrderHistory() }

    suspend fun getCurrentOrderList() = withContext(Dispatchers.IO) { repository.getCurrentOrderList() }

    suspend fun getPreferredOrderList() = withContext(Dispatchers.IO) { repository.getPreferredOrderList() }

    suspend fun userImageUpdate(
        header: String,
        photo: MultipartBody.Part,
        photo_name: RequestBody
    ) = withContext(Dispatchers.IO) { repository.userImageUpdate(header, photo, photo_name) }

    suspend fun userNameUpdate(username: String) = withContext(Dispatchers.IO) { repository.userNameUpdate(username) }

    suspend fun saveFcmToken(fcm_token: String) = withContext(Dispatchers.IO) { repository.saveFcmToken(fcm_token) }
    suspend fun deleteFcmToken() = withContext(Dispatchers.IO) { repository.deleteFcmToken() }

}