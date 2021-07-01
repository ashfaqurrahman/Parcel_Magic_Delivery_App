package com.airposted.bitoronbd_deliveryman.model

data class SendOTPDataModel(
    val sender_number: String,
    val token: String
)