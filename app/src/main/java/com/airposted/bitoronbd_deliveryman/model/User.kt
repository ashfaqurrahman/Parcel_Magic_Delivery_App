package com.airposted.bitoronbd_deliveryman.model

import com.google.gson.annotations.SerializedName

class User (
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("verified")
    val verified: Int?
)