package com.airposted.bitoronbd_deliveryman.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.ParcelRequestListItemBinding
import com.airposted.bitoronbd_deliveryman.databinding.PreferredOrderListItemBinding
import com.airposted.bitoronbd_deliveryman.model.PreferredAreaOrderListModelData

class PreferredOrderListRecyclerViewAdapter(
    private val preferredAreaOrderListModelData: List<PreferredAreaOrderListModelData>,
    private val preferredOrderListener: PreferredOrderClickListener
) : RecyclerView.Adapter<PreferredOrderListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: PreferredOrderListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.preferred_order_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = preferredAreaOrderListModelData[position]
        holder.bind(dataModel)
        holder.binding.viewOrder.setOnClickListener { preferredOrderListener.onItemClick(dataModel) }
    }

    override fun getItemCount(): Int {
        return preferredAreaOrderListModelData.size
    }

    inner class ViewHolder(var binding: PreferredOrderListItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.preferredOrderList, obj)
            binding.executePendingBindings()
        }
    }
}