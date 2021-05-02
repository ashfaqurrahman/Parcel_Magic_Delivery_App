package com.airposted.bitoronbd_deliveryman.view.main

import com.airposted.bitoronbd_deliveryman.model.OrderListModelData

interface OrderClickListener {
    fun onItemClick(order: OrderListModelData)
}