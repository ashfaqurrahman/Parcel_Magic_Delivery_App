package com.airposted.bitoronbd_deliveryman.view.main.pending_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.PendingOrderListItemBinding
import com.airposted.bitoronbd_deliveryman.model.LiveOrders

class PendingOrderRecyclerViewAdapter(
    private val liveOrders: ArrayList<LiveOrders>,
    private val listener: PendingOrderClickListener
) : RecyclerView.Adapter<PendingOrderRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: PendingOrderListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.pending_order_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = liveOrders[position]
        holder.bind(dataModel)

        holder.binding.accept.setOnClickListener { listener.onItemClick(dataModel) }

        var type = ""
        when(dataModel.type){
            1 -> type = "Envelop"
            2 -> type = "Small Box"
            3 -> type = "Large Box"
        }
        holder.binding.orderType.text = dataModel.qty.toString() + " " + type + ", " +
                dataModel.earning.toString() + "Tk"

    }

    override fun getItemCount(): Int {
        return liveOrders.size
    }

    inner class ViewHolder(var binding: PendingOrderListItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.liveOrders, obj)
            binding.executePendingBindings()
        }
    }
}