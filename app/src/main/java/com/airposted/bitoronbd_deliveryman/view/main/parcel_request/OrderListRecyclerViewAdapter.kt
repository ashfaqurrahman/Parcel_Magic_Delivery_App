package com.airposted.bitoronbd_deliveryman.view.main.parcel_request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.ParcelRequestListItemBinding
import com.airposted.bitoronbd_deliveryman.model.OrderListModelData

class OrderListRecyclerViewAdapter(
    private val orderListModelData: List<OrderListModelData>,
    private val listener: OrderClickListener
) : RecyclerView.Adapter<OrderListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ParcelRequestListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.parcel_request_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = orderListModelData[position]
        holder.bind(dataModel)
        holder.binding.viewOrder.setOnClickListener { listener.onItemClick(dataModel) }
    }

    override fun getItemCount(): Int {
        return orderListModelData.size
    }

    inner class ViewHolder(var binding: ParcelRequestListItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.orderList, obj)
            binding.executePendingBindings()
        }
    }
}