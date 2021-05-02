package com.airposted.bitoronbd_deliveryman.model

data class StatusChangeModel(
    val `data`: List<Any>,
    val msg: String,
    val status: Int,
    val success: Boolean
)