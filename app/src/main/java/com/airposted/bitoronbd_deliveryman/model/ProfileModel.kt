package com.airposted.bitoronbd_deliveryman.model

data class ProfileModel(
    val `data`: List<ProfileModelData>,
    val msg: String,
    val status: Int,
    val success: Boolean
)