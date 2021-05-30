package com.airposted.bitoronbd_deliveryman.view.main

import com.airposted.bitoronbd_deliveryman.model.ViewMyAreaModelData

interface MyAreaClickListener {
    fun onItemDeleteListener(area: ViewMyAreaModelData)
    fun onItemClickListener(area: ViewMyAreaModelData)
}