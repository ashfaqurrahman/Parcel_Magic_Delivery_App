package com.airposted.bitoronbd_deliveryman.model

data class SendOTPModel(
    val `data`: SendOTPDataModel,
    val msg: String,
    val status: Int,
    val success: Boolean
)