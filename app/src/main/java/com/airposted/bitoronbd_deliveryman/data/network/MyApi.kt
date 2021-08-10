package com.airposted.bitoronbd_deliveryman.data.network

import com.airposted.bitoronbd_deliveryman.model.*
import com.airposted.bitoronbd_deliveryman.model.auth.AuthResponse
import com.airposted.bitoronbd_deliveryman.model.my_current_area.MyCurrentArea
import com.airposted.bitoronbd_deliveryman.model.rating.AverageRatingModel
import com.airposted.bitoronbd_deliveryman.model.register.SignUpModel
import com.airposted.bitoronbd_deliveryman.model.wallet.WalletModel
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {

    @FormUrlEncoded
    @POST("driver-phonenumber-check")
    suspend fun numberCheck(
        @Field("phone") phone: String
    ) : Response<AuthResponse>

    @FormUrlEncoded
    @POST("send-otp-message")
    suspend fun sendOTP(
        @Field("phone_number") phone: String
    ) : Response<AuthResponse>

    @Multipart
    @POST("register-delivery")
    suspend fun userSignUpWithNid(
        @Part("username") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part nidFile: MultipartBody.Part,
        @Part("nid") nid: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("address") address: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ) : Response<SignUpModel>

    @Multipart
    @POST("register-delivery")
    suspend fun userSignUpWithDriveLicense(
        @Part("username") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part licenceFile: MultipartBody.Part,
        @Part("drive_lisence") drivingLicence: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("address") address: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ) : Response<SignUpModel>

    @GET("delivery/area")
    suspend fun getAllAreaList(
        @Header("Authorization") header: String
    ) : Response<AreaListDataModel>

    @FormUrlEncoded
    @POST("delivery/prefered-area-list-add")
    suspend fun addMyArea(
        @Header("Authorization") header: String,
        @Field("area_id") areaId: Int
    ) : Response<RequestModel>

    @POST("delivery/prefered-area-list-view")
    suspend fun viewMyArea(
        @Header("Authorization") header: String
    ) : Response<ViewMyAreaModel>

    @FormUrlEncoded
    @POST("delivery/prefered-area-list-delete")
    suspend fun deleteMyArea(
        @Header("Authorization") header: String,
        @Field("prefered_area_id") id: Int
    ) : Response<RequestModel>

    @FormUrlEncoded
    @POST("delivery/single-search-order-list")
    suspend fun getOrderList(
        @Header("Authorization") header: String,
        @Field("to") to: Int,
        @Field("driver_latitude") latitude: String,
        @Field("driver_longitude") longitude: String
    ) : Response<OrderListModel>

    @FormUrlEncoded
    @POST("delivery/single-search-order-list")
    suspend fun getOrderListByArea(
        @Header("Authorization") header: String,
        @Field("to") to: Int,
        @Field("driver_latitude") latitude: String,
        @Field("driver_longitude") longitude: String
    ) : Response<OrderListModel>

    @FormUrlEncoded
    @POST("delivery/my-current-area")
    suspend fun getMyCurrentArea(
        @Header("Authorization") header: String,
        @Field("driver_latitude") latitude: String,
        @Field("driver_longitude") longitude: String
    ) : Response<MyCurrentArea>

    @FormUrlEncoded
    @POST("delivery/all-order-list")
    suspend fun getAllOrders(
        @Header("Authorization") header: String,
        @Field("driver_latitude") latitude: String,
        @Field("driver_longitude") longitude: String
    ) : Response<MyCurrentArea>

    @GET("delivery/orderdetails/{id}")
    suspend fun getOrderDetails(
        @Header("Authorization") header: String,
        @Path("id") id: String
    ): Response<OrderDetailsModel>

    @POST("delivery/order-history")
    suspend fun myOrderHistory(
        @Header("Authorization") header: String
    ) : Response<OrderListModel>

    @POST("delivery/delivery-current-orderlist")
    suspend fun getCurrentOrderList(
        @Header("Authorization") header: String
    ) : Response<OrderListModel>

    @POST("delivery/prefered-area-order-list")
    suspend fun getPreferredOrderList(
        @Header("Authorization") header: String
    ) : Response<PreferredAreaOrderListModel>

    @POST("delivery/profile")
    suspend fun myProfile(
        @Header("Authorization") header: String
    ) : Response<ProfileModel>

    @GET("delivery/get-average-rating")
    suspend fun getAverageRating(
        @Header("Authorization") header: String
    ) : Response<AverageRatingModel>

    @GET("delivery/wallet")
    suspend fun myWallet(
        @Header("Authorization") header: String
    ) : Response<WalletModel>


    @FormUrlEncoded
    @POST("delivery/delivery-user-update")
    suspend fun userNameUpdate(
        @Header("Authorization") header: String,
        @Field("username") username: String
    ) : Response<ProfileModel>

    @FormUrlEncoded
    @POST("delivery/order-status-change")
    suspend fun changeStatus(
        @Header("Authorization") header: String,
        @Field("invoice_no") invoice: String,
        @Field("current_status") status: Int,
        @Field("otp") otp: Int
    ) : Response<StatusChangeModel>

    @Multipart
    @POST("delivery/delivery-user-update")
    suspend fun userImageUpdate(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ) : Response<ProfileModel>

    @FormUrlEncoded
    @POST("delivery/add-fcmtoken")
    suspend fun saveFcmToken(
        @Header("Authorization") header: String,
        @Field("fcm_token") fcm_token: String,
    ): Response<RequestModel>

    @GET("delivery/delete-fcm-token")
    suspend fun deleteFcmToken(
        @Header("Authorization") header: String
    ): Response<RequestModel>

    companion object{
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : MyApi{

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl("https://api.parcelmagic.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}

