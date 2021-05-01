package com.airposted.bitoronbd_deliveryman.data.repositories

import android.content.Context
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.SafeApiRequest
import com.airposted.bitoronbd_deliveryman.model.AreaListDataModel

class HomeRepository (
    context: Context,
    private val api: MyApi
) : SafeApiRequest() {
    private val appContext = context.applicationContext

    suspend fun getAllAreaList(): AreaListDataModel {
        return apiRequest { api.getAllAreaList(
            PersistentUser.getInstance().getAccessToken(
            appContext
        )) }
    }

}