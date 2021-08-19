package com.airposted.bitoronbd_deliveryman.view.main.pending_order

import com.airposted.bitoronbd_deliveryman.model.LiveOrders

interface PendingOrderClickListener {
    fun onItemClick(liveOrders: LiveOrders)
}