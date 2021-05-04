package com.airposted.bitoronbd_deliveryman.data.repositories

import android.content.Context
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.SafeApiRequest
import com.airposted.bitoronbd_deliveryman.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeRepository(
    context: Context,
    private val api: MyApi
) : SafeApiRequest() {
    private val appContext = context.applicationContext

    suspend fun getAllAreaList(): AreaListDataModel {
        return apiRequest {
            api.getAllAreaList(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }

    suspend fun addMyArea(areaId: Int): AddMyAreaModel {
        return apiRequest {
            api.addMyArea(
                PersistentUser.getInstance().getAccessToken(appContext),
                areaId
            )
        }
    }

    suspend fun viewMyArea(): ViewMyAreaModel {
        return apiRequest {
            api.viewMyArea(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }

    suspend fun deleteMyArea(id: Int): AddMyAreaModel {
        return apiRequest {
            api.deleteMyArea(
                PersistentUser.getInstance().getAccessToken(appContext), id
            )
        }
    }

    suspend fun myProfile(): ProfileModel {
        return apiRequest {
            api.myProfile(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }

    suspend fun getOrderList(to: Int, from: Int): OrderListModel {
        return apiRequest {
            api.getOrderList(
                PersistentUser.getInstance().getAccessToken(appContext), to, from
            )
        }
    }

    suspend fun getOrderDetails(id: String): OrderDetailsModel {
        return apiRequest {
            api.getOrderDetails(
                PersistentUser.getInstance().getAccessToken(appContext), id
            )
        }
    }

    suspend fun changeStatus(invoice: String, status: Int): StatusChangeModel {
        return apiRequest {
            api.changeStatus(
                PersistentUser.getInstance().getAccessToken(appContext), invoice, status
            )
        }
    }

    suspend fun myOrderHistory(): OrderListModel {
        return apiRequest {
            api.myOrderHistory(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }

    suspend fun getCurrentOrderList(): OrderListModel {
        return apiRequest {
            api.getCurrentOrderList(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }

    suspend fun getPreferredOrderList(): PreferredAreaOrderListModel {
        return apiRequest {
            api.getPreferredOrderList(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }

    suspend fun userImageUpdate(
        header: String,
        photo: MultipartBody.Part,
        photo_name: RequestBody
    ) : ProfileModel {
        return apiRequest { api.userImageUpdate(header, photo, photo_name)}
    }

    suspend fun userNameUpdate(username: String): ProfileModel {
        return apiRequest {
            api.userNameUpdate(
                PersistentUser.getInstance().getAccessToken(appContext), username
            )
        }
    }
}