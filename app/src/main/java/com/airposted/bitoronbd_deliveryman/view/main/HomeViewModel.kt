package com.airposted.bitoronbd_deliveryman.view.main

import androidx.lifecycle.ViewModel
import com.airposted.bitoronbd_deliveryman.data.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    suspend fun getAllAreaList() = withContext(Dispatchers.IO) { repository.getAllAreaList() }

}