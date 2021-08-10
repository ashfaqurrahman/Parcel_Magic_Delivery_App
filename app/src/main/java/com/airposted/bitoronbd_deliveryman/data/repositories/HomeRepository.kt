package com.airposted.bitoronbd_deliveryman.data.repositories

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.SafeApiRequest
import com.airposted.bitoronbd_deliveryman.data.network.preferences.PreferenceProvider
import com.airposted.bitoronbd_deliveryman.model.*
import com.airposted.bitoronbd_deliveryman.model.all_orders.AllOrders
import com.airposted.bitoronbd_deliveryman.model.my_current_area.MyCurrentArea
import com.airposted.bitoronbd_deliveryman.model.rating.AverageRatingModel
import com.airposted.bitoronbd_deliveryman.model.wallet.WalletModel
import com.google.android.gms.location.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.util.*

class HomeRepository(
    context: Context,
    private val api: MyApi
) : SafeApiRequest(),
    LocationListener {
    private val appContext = context.applicationContext
    private val gps = MutableLiveData<Boolean>()
    private var mLocationManager: LocationManager? = null

    private var mLocationRequest: LocationRequest? = null
    var mLastLocation: Location? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val userName = MutableLiveData<String>()
    val userImage = MutableLiveData<String>()

    init {
        mLocationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                appContext,
                permissions[0]
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100F, this)
        }
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

    suspend fun getOrderList(to: Int): OrderListModel {
        return apiRequest {
            api.getOrderList(
                PersistentUser.getInstance().getAccessToken(appContext),
                to,
                PreferenceProvider(appContext).getSharedPreferences("latitude").toString(),
                PreferenceProvider(appContext).getSharedPreferences("longitude").toString()
            )
        }
    }

    suspend fun getOrderListByArea(to: Int): OrderListModel {
        return apiRequest {
            api.getOrderListByArea(
                PersistentUser.getInstance().getAccessToken(appContext),
                to,
                PreferenceProvider(appContext).getSharedPreferences("latitude").toString(),
                PreferenceProvider(appContext).getSharedPreferences("longitude").toString()
            )
        }
    }

    suspend fun getMyCurrentArea(): MyCurrentArea {
        return apiRequest {
            api.getMyCurrentArea(
                PersistentUser.getInstance().getAccessToken(appContext),
                PreferenceProvider(appContext).getSharedPreferences("latitude").toString(),
                PreferenceProvider(appContext).getSharedPreferences("longitude").toString()
            )
        }
    }

    suspend fun getAllOrders(): AllOrders {
        return apiRequest {
            api.getAllOrders(
                PersistentUser.getInstance().getAccessToken(appContext),
                PreferenceProvider(appContext).getSharedPreferences("latitude").toString(),
                PreferenceProvider(appContext).getSharedPreferences("longitude").toString()
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

    fun location(): LiveData<Boolean> {
        return gps
    }

    override fun onLocationChanged(location: Location) {
        PreferenceProvider(appContext).saveSharedPreferences("latitude", location.latitude.toString())
        PreferenceProvider(appContext).saveSharedPreferences("longitude", location.longitude.toString())
        val geo = Geocoder(appContext, Locale.ENGLISH)
        try {
            val addresses = geo.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.isNotEmpty()) {
                PreferenceProvider(appContext).saveSharedPreferences("location", addresses[0].subLocality)
                gps.postValue(true)
            }
        } catch (e: InvocationTargetException) {
            Timber.e("Location not found")
        }

    }

    override fun onProviderEnabled(provider: String) {
        if (provider == LocationManager.GPS_PROVIDER) {
            gps.postValue(true)
            locationCallback()
        }
    }

    override fun onProviderDisabled(provider: String) {
        if (provider == LocationManager.GPS_PROVIDER) {
            gps.postValue(false)
            dismiss()
        }
    }

    fun locationCallback() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        mLocationRequest = LocationRequest()

        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        }
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                mLastLocation = location
                PreferenceProvider(appContext).saveSharedPreferences("latitude", location.latitude.toString())
                PreferenceProvider(appContext).saveSharedPreferences("longitude", location.longitude.toString())
                gps.postValue(true)
            }
        }
    }

    private fun dismiss() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
    }
}