package com.airposted.bitoronbd_deliveryman.view.main.preferred_order

import com.airposted.bitoronbd_deliveryman.model.PreferredAreaOrderListModelData

interface PreferredOrderClickListener {
    fun onItemClick(preferredOrder: PreferredAreaOrderListModelData)
}