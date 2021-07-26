package com.airposted.bitoronbd_deliveryman.model.register

data class User(
    val active: Int,
    val id: Int,
    val image: Any,
    val phone: String,
    val username: String
)