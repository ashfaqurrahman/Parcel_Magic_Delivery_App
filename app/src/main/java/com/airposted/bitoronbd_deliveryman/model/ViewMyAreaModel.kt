package com.airposted.bitoronbd_deliveryman.model

data class ViewMyAreaModel(
    val `data`: List<ViewMyAreaModelData>,
    val msg: String,
    val status: Int,
    val success: Boolean
)