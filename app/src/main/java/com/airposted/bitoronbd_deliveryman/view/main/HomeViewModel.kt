package com.airposted.bitoronbd_deliveryman.view.main

import androidx.lifecycle.ViewModel
import com.airposted.bitoronbd_deliveryman.data.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    suspend fun getAllAreaList() = withContext(Dispatchers.IO) { repository.getAllAreaList() }

    suspend fun addMyArea(areaId: Int) = withContext(Dispatchers.IO) { repository.addMyArea(areaId) }

    suspend fun viewMyArea() = withContext(Dispatchers.IO) { repository.viewMyArea() }

    suspend fun deleteMyArea(id: Int) = withContext(Dispatchers.IO) { repository.deleteMyArea(id) }

    suspend fun myProfile() = withContext(Dispatchers.IO) { repository.myProfile() }

    suspend fun getOrderList(to: Int, from: Int) = withContext(Dispatchers.IO) { repository.getOrderList(to, from) }

    suspend fun getOrderDetails(
        id: String
    ) = withContext(Dispatchers.IO) { repository.getOrderDetails(id) }

    suspend fun changeStatus(
        invoice: String,
        status: Int
    ) = withContext(Dispatchers.IO) { repository.changeStatus(invoice, status) }

    suspend fun myOrderHistory() = withContext(Dispatchers.IO) { repository.myOrderHistory() }

    suspend fun getCurrentOrderList() = withContext(Dispatchers.IO) { repository.getCurrentOrderList() }

}