package com.airposted.bitoronbd_deliveryman.model.register

data class SignUpModel(
    val `data`: Data?,
    val msg: String,
    val status: Int,
    val success: Boolean,
    val user: User?
)