package com.airposted.bitoronbd_deliveryman.view.main.parcel_request

import com.airposted.bitoronbd_deliveryman.model.OrderListModelData

interface OrderClickListener {
    fun onItemClick(order: OrderListModelData)
}