package com.airposted.bitoronbd_deliveryman.model

data class OrderDetailsModel(
    val `data`: List<OrderDetailsModelData>,
    val msg: String,
    val status: Int,
    val success: Boolean
)