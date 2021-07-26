package com.airposted.bitoronbd_deliveryman.model.rating

data class AverageRatingModel(
    val `data`: List<Data>,
    val msg: String,
    val status: Int,
    val success: Boolean
)