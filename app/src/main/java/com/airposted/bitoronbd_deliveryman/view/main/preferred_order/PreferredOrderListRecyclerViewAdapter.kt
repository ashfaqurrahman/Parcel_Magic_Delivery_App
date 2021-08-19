package com.airposted.bitoronbd_deliveryman.view.main.preferred_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
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

        when (dataModel.personal_order_type) {
            1 -> {
                holder.binding.orderType.text = "Regular Delivery"
            }
            2 -> {
                holder.binding.orderType.text = "Express Delivery"
            }
            else -> {
                holder.binding.orderType.text = "null"
            }
        }

        when (dataModel.item_type) {
            1 -> {
                holder.binding.parcelTypeIcon.setImageResource(R.drawable.ic_envelope_icon)
            }
            2 -> {
                holder.binding.parcelTypeIcon.setImageResource(R.drawable.ic_box_icon)
            }
            3 -> {
                holder.binding.parcelTypeIcon.setImageResource(R.drawable.ic_box_icon)
            }
        }

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