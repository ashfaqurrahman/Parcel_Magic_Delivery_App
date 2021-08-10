package com.airposted.bitoronbd_deliveryman.view.main.all_orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.AllOrderListItemBinding
import com.airposted.bitoronbd_deliveryman.model.all_orders.Data

class AllOrderListRecyclerViewAdapter(
    private val allOrders: List<Data>,
    private val allOrdersListener: AllOrderClickListener
) : RecyclerView.Adapter<AllOrderListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: AllOrderListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.all_order_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = allOrders[position]
        holder.bind(dataModel)
        holder.binding.viewOrder.setOnClickListener { allOrdersListener.onItemClick(dataModel) }
    }

    override fun getItemCount(): Int {
        return allOrders.size
    }

    inner class ViewHolder(var binding: AllOrderListItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.data, obj)
            binding.executePendingBindings()
        }
    }
}