package com.airposted.bitoronbd_deliveryman.model.register

data class User(
    val id: Int,
    val username: String,
    val phone: String,
    val image: String,
    val active: Int
)