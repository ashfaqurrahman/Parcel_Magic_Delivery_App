package com.airposted.bitoronbd_deliveryman.model

data class WalletData(
    val current_page: Int,
    val `data`: List<WalletDataModel>,
    val from: Int,
    val last_page: Int,
    val next_page_url: Any,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)