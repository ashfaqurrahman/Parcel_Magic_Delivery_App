package com.airposted.bitoronbd_deliveryman.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.MyDeliveryHistoryListItemBinding
import com.airposted.bitoronbd_deliveryman.databinding.MyLiveDeliveryListItemBinding
import com.airposted.bitoronbd_deliveryman.model.OrderListModelData

class CurrentOrderListRecyclerViewAdapter(
    private val currentOrderListModelData: List<OrderListModelData>,
    private val listener: CurrentOrderClickListener
) : RecyclerView.Adapter<CurrentOrderListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: MyLiveDeliveryListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.my_live_delivery_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = currentOrderListModelData[position]
        holder.bind(dataModel)
        holder.binding.viewOrder.setOnClickListener { listener.onItemClick(dataModel) }
    }

    override fun getItemCount(): Int {
        return currentOrderListModelData.size
    }

    inner class ViewHolder(var binding: MyLiveDeliveryListItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.currentOrderList, obj)
            binding.executePendingBindings()
        }
    }
}