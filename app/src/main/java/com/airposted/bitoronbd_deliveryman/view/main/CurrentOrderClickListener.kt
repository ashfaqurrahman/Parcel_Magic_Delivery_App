package com.airposted.bitoronbd_deliveryman.view.main

import com.airposted.bitoronbd_deliveryman.model.OrderListModelData

interface CurrentOrderClickListener {
    fun onItemClick(currentOrder: OrderListModelData)
}