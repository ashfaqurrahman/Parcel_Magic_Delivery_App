package com.airposted.bitoronbd_deliveryman.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("token")
    val token: String,
    @SerializedName("token_type")
    val tokenType: String
)