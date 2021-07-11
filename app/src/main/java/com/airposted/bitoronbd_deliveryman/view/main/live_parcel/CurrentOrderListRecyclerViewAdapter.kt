package com.airposted.bitoronbd_deliveryman.view.main.live_parcel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.MyLiveDeliveryListItemBinding
import com.airposted.bitoronbd_deliveryman.model.OrderListModelData

class CurrentOrderListRecyclerViewAdapter(
    private val currentOrderListModelData: List<OrderListModelData>,
    private val viewDetailsListener: CurrentOrderClickListener
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
        when(dataModel.current_status) {
            3 -> {
                holder.binding.imgAccept.alpha = 1F
                holder.binding.tvAccept.alpha = 1F
                holder.binding.imgCollected.alpha = 0.3F
                holder.binding.tvCollected.alpha = 0.3F
                holder.binding.imgDelivered.alpha = 0.3F
                holder.binding.tvDelivered.alpha = 0.3F

                holder.binding.name.text = dataModel.pic_name
                holder.binding.calling.setOnClickListener { viewDetailsListener.onCallClick(dataModel.pic_phone) }
            }
            4 -> {
                holder.binding.imgAccept.alpha = 0.3F
                holder.binding.tvAccept.alpha = 0.3F
                holder.binding.imgCollected.alpha = 1F
                holder.binding.tvCollected.alpha = 1F
                holder.binding.imgDelivered.alpha = 0.3F
                holder.binding.tvDelivered.alpha = 0.3F

                holder.binding.name.text = dataModel.recp_name
                holder.binding.calling.setOnClickListener { viewDetailsListener.onCallClick(dataModel.recp_phone) }
            }
            5 -> {
                holder.binding.imgAccept.alpha = 0.3F
                holder.binding.tvAccept.alpha = 0.3F
                holder.binding.imgCollected.alpha = 0.3F
                holder.binding.tvCollected.alpha = 0.3F
                holder.binding.imgDelivered.alpha = 1F
                holder.binding.tvDelivered.alpha = 1F
            }
        }
        holder.binding.viewOrder.setOnClickListener { viewDetailsListener.onItemClick(dataModel) }
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