package com.airposted.bitoronbd_deliveryman.model

data class OrderListModelData(
    val cancel_fee: Int,
    val cancelled_date: Any,
    val cartoon_box_type: Int,
    val coc: Int,
    val cod: Int,
    val collection_date: Any,
    val created_at: String,
    val current_status: Int,
    val delivery_charge: Int,
    val delivery_date: String,
    val dimention: Int,
    val id: Int,
    val invoice_no: String,
    val logistics_charge: Int,
    val order_date: String,
    val order_type: Int,
    val personal_order_type: Any,
    val refund_fee: Any,
    val shop_id: Any,
    val ssl_transaction_id: Any,
    val total_price: Any,
    val updated_at: String,
    val user_id: Int,
    val who_will_pay: Int
)