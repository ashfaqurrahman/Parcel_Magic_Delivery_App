package com.airposted.bitoronbd_deliveryman.model

data class LiveOrders(
    val driver_type: Int = 0,
    val earning: Double = 0.0,
    val invoice_no: String = "",
    val order_date: String = "",
    val receiver_address: String = "",
    val receiver_area: Int = 0,
    val sender_address: String = "",
    val sender_area: Int = 0,
    val sender_lat: Double = 0.0,
    val sender_long: Double = 0.0,
    val receiver_lat: Double = 0.0,
    val receiver_long: Double = 0.0,
    val distance: Double = 0.0,
    val qty: Int = 0,
    val type: Int = 0
)