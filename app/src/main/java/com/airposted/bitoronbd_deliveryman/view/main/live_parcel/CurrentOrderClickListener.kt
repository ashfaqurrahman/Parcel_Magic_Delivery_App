package com.airposted.bitoronbd_deliveryman.view.main.live_parcel

import com.airposted.bitoronbd_deliveryman.model.OrderListModelData

interface CurrentOrderClickListener {
    fun onItemClick(currentOrder: OrderListModelData)
    fun onCallClick(phone: String)
}