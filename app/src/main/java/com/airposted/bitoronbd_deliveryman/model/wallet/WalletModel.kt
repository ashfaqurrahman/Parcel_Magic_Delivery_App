package com.airposted.bitoronbd_deliveryman.model.wallet

data class WalletModel(
    val `data`: List<Data>,
    val msg: String,
    val status: Int,
    val success: Boolean,
    val wallet_info: WalletInfo
)