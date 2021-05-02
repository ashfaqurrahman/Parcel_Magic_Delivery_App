package com.airposted.bitoronbd_deliveryman.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.MyAreaListItemBinding
import com.airposted.bitoronbd_deliveryman.model.ViewMyAreaModelData

class MyAreaListRecyclerViewAdapter(
    private val areaListDataModelModelList: List<ViewMyAreaModelData>,
    private val listener: MyAreaClickListener
) : RecyclerView.Adapter<MyAreaListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: MyAreaListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.my_area_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = areaListDataModelModelList[position]
        holder.bind(dataModel)
        holder.binding.delete.setOnClickListener { listener.onItemClick(dataModel) }
    }

    override fun getItemCount(): Int {
        return areaListDataModelModelList.size
    }

    inner class ViewHolder(var binding: MyAreaListItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.myArea, obj)
            binding.executePendingBindings()
        }
    }
}