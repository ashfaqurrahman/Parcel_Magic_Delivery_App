package com.airposted.bitoronbd_deliveryman.data.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.SafeApiRequest
import com.airposted.bitoronbd_deliveryman.model.*
import com.airposted.bitoronbd_deliveryman.model.rating.AverageRatingModel
import com.airposted.bitoronbd_deliveryman.model.wallet.WalletModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeRepository(
    context: Context,
    private val api: MyApi
) : SafeApiRequest() {
    private val appContext = context.applicationContext

    val userName = MutableLiveData<String>()
    val userImage = MutableLiveData<String>()

    init {
        getName()
        getImage()
    }

    private fun getName() {
        userName.postValue(PersistentUser.getInstance().getFullName(appContext))
    }

    private fun getImage() {
        userImage.postValue(PersistentUser.getInstance().getUserImage(appContext))
    }

    suspend fun getAllAreaList(): AreaListDataModel {
        return apiRequest {
            api.getAllAreaList(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }

    suspend fun addMyArea(areaId: Int): RequestModel {
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

    suspend fun deleteMyArea(id: Int): RequestModel {
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

    suspend fun getAverageRating(): AverageRatingModel {
        return apiRequest {
            api.getAverageRating(
                PersistentUser.getInstance().getAccessToken(appContext)
            )
        }
    }

    suspend fun myWallet(): WalletModel {
        return apiRequest {
            api.myWallet(
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

    suspend fun getOrderListByArea(to: Int): OrderListModel {
        return apiRequest {
            api.getOrderListByArea(
                PersistentUser.getInstance().getAccessToken(appContext), to
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

    suspend fun changeStatus(invoice: String, status: Int, otp: Int): StatusChangeModel {
        return apiRequest {
            api.changeStatus(
                PersistentUser.getInstance().getAccessToken(appContext), invoice, status, otp
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
    ): ProfileModel {
        val response = apiRequest { api.userImageUpdate(header, photo, photo_name) }
        PersistentUser.getInstance().setUserImage(appContext, response.data.image)
        userImage.postValue(response.data.image)
        return response
    }

    suspend fun userNameUpdate(username: String): ProfileModel {
        val response = apiRequest {
            api.userNameUpdate(
                PersistentUser.getInstance().getAccessToken(appContext), username
            )
        }

        PersistentUser.getInstance().setFullname(appContext, response.data.username)
        userName.postValue(response.data.username)

        return response
    }

    suspend fun saveFcmToken(fcm_token: String): RequestModel {
        return apiRequest {
            api.saveFcmToken(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                ), fcm_token
            )
        }
    }

    suspend fun deleteFcmToken(): RequestModel {
        return apiRequest {
            api.deleteFcmToken(
                PersistentUser.getInstance().getAccessToken(
                    appContext
                )
            )
        }
    }
}