package com.airposted.bitoronbd_deliveryman.model

data class WalletModel(
    val `data`: WalletData,
    val msg: String,
    val status: Int,
    val success: Boolean,
    val wallet_info: WalletInfo
)