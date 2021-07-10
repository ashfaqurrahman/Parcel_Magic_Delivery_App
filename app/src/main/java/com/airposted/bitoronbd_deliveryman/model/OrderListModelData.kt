package com.airposted.bitoronbd_deliveryman.model

data class OrderListModelData(
    val cancel_fee: Int,
    val cancelled_date: String,
    val cartoon_box_type: Int,
    val coc: Int,
    val cod: Int,
    val collection_date: Any,
    val created_at: String,
    val current_status: Int,
    val delivery_charge: Double,
    val delivery_date: String,
    val delivery_type: Int,
    val dimention: Int,
    val id: Int,
    val invoice_no: String,
    val item_des: String,
    val order_item_name: String,
    val item_price: Int,
    val item_qty: Int,
    val item_type: Int,
    val item_weight: Int,
    val logistics_charge: Double,
    val order_date: String,
    val order_type: Int,
    val parcel_type: Int,
    val personal_order_type: Int,
    val pick_address: String,
    val pick_area_id: Int,
    val pick_city: String,
    val pick_zone: String,
    val recp_address: String,
    val recp_area: String,
    val recp_city: String,
    val recp_name: String,
    val recp_phone: String,
    val recp_zone: String,
    val refund_fee: Any,
    val shop_id: Int,
    val active: Int,
    val special_instruction: String,
    val ssl_transaction_id: String,
    val total_price: Double,
    val updated_at: String,
    val pic_name: String,
    val pic_phone: String,
    val bill_invoice: String,
    val user_id: Int,
    val otp: Int,
    val billing_status: Int,
    val distance: Double,
    val sender_latitude: Double,
    val sender_longitude: Double,
    val receiver_latitude: Double,
    val receiver_longitude: Double,
    val who_will_pay: Int
)