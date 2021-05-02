package com.airposted.bitoronbd_deliveryman.model

data class PreferredAreaOrderListModel(
    val `data`: List<PreferredAreaOrderListModelData>,
    val msg: String,
    val status: Int,
    val success: Boolean
)