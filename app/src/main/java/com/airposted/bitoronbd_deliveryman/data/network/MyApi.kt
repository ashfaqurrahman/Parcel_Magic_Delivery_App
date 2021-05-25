package com.airposted.bitoronbd_deliveryman.data.network

import com.airposted.bitoronbd_deliveryman.data.network.responses.AuthResponse
import com.airposted.bitoronbd_deliveryman.model.*
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
    @POST("phonenumbercheck")
    suspend fun numberCheck(
        @Field("phone") email: String
    ) : Response<AuthResponse>

    @Multipart
    @POST("register_delivery")
    suspend fun userSignUpWithNid(
        @Part("username") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("nid") nid: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("address") address: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ) : Response<AuthResponse>

    @Multipart
    @POST("register_delivery")
    suspend fun userSignUpWithDriveLicense(
        @Part("username") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("drive_lisence") drivingLicence: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("address") address: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ) : Response<AuthResponse>

    @GET("delivery/area")
    suspend fun getAllAreaList(
        @Header("Authorization") header: String
    ) : Response<AreaListDataModel>

    @FormUrlEncoded
    @POST("delivery/prefered_area_list_add")
    suspend fun addMyArea(
        @Header("Authorization") header: String,
        @Field("area_id") areaId: Int
    ) : Response<AddMyAreaModel>

    @POST("delivery/prefered_area_list_view")
    suspend fun viewMyArea(
        @Header("Authorization") header: String
    ) : Response<ViewMyAreaModel>

    @FormUrlEncoded
    @POST("delivery/prefered_area_list_delete")
    suspend fun deleteMyArea(
        @Header("Authorization") header: String,
        @Field("prefered_area_id") id: Int
    ) : Response<AddMyAreaModel>

    @FormUrlEncoded
    @POST("delivery/search_order_list")
    suspend fun getOrderList(
        @Header("Authorization") header: String,
        @Field("to") to: Int,
        @Field("from") from: Int
    ) : Response<OrderListModel>

    @GET("delivery/orderdetails/{id}")
    suspend fun getOrderDetails(
        @Header("Authorization") header: String,
        @Path("id") id: String
    ): Response<OrderDetailsModel>

    @POST("delivery/order_history")
    suspend fun myOrderHistory(
        @Header("Authorization") header: String
    ) : Response<OrderListModel>

    @POST("delivery/deliverycurrentorderlist")
    suspend fun getCurrentOrderList(
        @Header("Authorization") header: String
    ) : Response<OrderListModel>

    @POST("delivery/prefered_area_order_list")
    suspend fun getPreferredOrderList(
        @Header("Authorization") header: String
    ) : Response<PreferredAreaOrderListModel>

    @POST("delivery/profile")
    suspend fun myProfile(
        @Header("Authorization") header: String
    ) : Response<ProfileModel>


    @FormUrlEncoded
    @POST("delivery/delivery_user_update")
    suspend fun userNameUpdate(
        @Header("Authorization") header: String,
        @Field("username") username: String
    ) : Response<ProfileModel>

    @FormUrlEncoded
    @POST("delivery/orderstatuschange")
    suspend fun changeStatus(
        @Header("Authorization") header: String,
        @Field("invoice_no") invoice: String,
        @Field("current_status") status: Int,
        @Field("otp") otp: Int
    ) : Response<StatusChangeModel>

    @Multipart
    @POST("delivery/delivery_user_update")
    suspend fun userImageUpdate(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("image") requestBody: RequestBody
    ) : Response<ProfileModel>

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
                .baseUrl("https://parcel.airposted.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}

