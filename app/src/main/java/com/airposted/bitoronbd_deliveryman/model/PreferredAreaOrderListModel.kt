package com.airposted.bitoronbd_deliveryman.model

data class PreferredAreaOrderListModel(
    val status: Int,
    val success: Boolean,
    val msg: String,
    val `data`: List<PreferredAreaOrderListModelData>
)