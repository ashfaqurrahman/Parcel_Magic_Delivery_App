package com.airposted.bitoronbd_deliveryman.model.all_orders

data class Data(
    val active: Any,
    val bill_invoice: Any,
    val billing_status: Int,
    val cancel_fee: Int,
    val cancelled_date: Any,
    val cartoon_box_type: Int,
    val coc: Int,
    val cod: Int,
    val collection_date: Any,
    val created_at: String,
    val current_status: Int,
    val delivery_charge: Double,
    val delivery_date: String,
    val delivery_type: Any,
    val dimention: Int,
    val distance: Double,
    val id: Int,
    val invoice_no: String,
    val isRated: Int,
    val item_des: Any,
    val item_price: Any,
    val item_qty: Int,
    val item_type: Int,
    val item_weight: Int,
    val logistics_charge: Double,
    val order_date: String,
    val order_item_name: String,
    val order_type: Int,
    val otp: Any,
    val parcel_type: Any,
    val personal_order_type: Int,
    val pic_name: String,
    val pic_phone: String,
    val pick_address: String,
    val pick_area_id: Any,
    val pick_city: Any,
    val pick_zone: Any,
    val receiver_latitude: Double,
    val receiver_longitude: Double,
    val recp_address: String,
    val recp_area: Any,
    val recp_city: Any,
    val recp_name: String,
    val recp_phone: String,
    val recp_zone: Any,
    val refund_fee: Any,
    val sender_latitude: Double,
    val sender_longitude: Double,
    val shop_id: Any,
    val special_instruction: Any,
    val total_price: Any,
    val updated_at: String,
    val user_id: Int,
    val who_will_pay: Int
)