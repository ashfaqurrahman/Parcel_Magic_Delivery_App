package com.airposted.bitoronbd_deliveryman.model

data class ProfileModel(
    val `data`: ProfileModelData,
    val msg: String,
    val status: Int,
    val success: Boolean
)