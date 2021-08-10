package com.airposted.bitoronbd_deliveryman.model.all_orders

data class AllOrders(
    val `data`: List<Data>,
    val msg: String,
    val preferred_area_order_list: List<String>,
    val status: Int,
    val success: Boolean
)