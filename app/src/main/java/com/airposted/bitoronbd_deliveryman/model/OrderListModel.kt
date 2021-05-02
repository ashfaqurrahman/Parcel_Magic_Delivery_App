package com.airposted.bitoronbd_deliveryman.model

data class OrderListModel(
    val `data`: List<OrderListModelData>,
    val msg: String,
    val status: Int,
    val success: Boolean
)