package com.airposted.bitoronbd_deliveryman.model

data class RequestModel(
    val msg: String,
    val status: Int,
    val success: Boolean
)