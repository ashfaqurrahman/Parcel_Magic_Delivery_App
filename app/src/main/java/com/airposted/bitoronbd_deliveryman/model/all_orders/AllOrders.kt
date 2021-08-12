package com.airposted.bitoronbd_deliveryman.model.all_orders

data class AllOrders(
    val `data`: List<Data>?,
    val msg: String,
    val preferredAreaOrderList: List<PreferredAreaOrder>,
    val status: Int,
    val success: Boolean
)