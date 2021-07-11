package com.airposted.bitoronbd_deliveryman.view.main.preferred_area

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.AreaListItemBinding
import com.airposted.bitoronbd_deliveryman.model.AreaListDataModelData

class AreaListRecyclerViewAdapter(
    private val areaListDataModelModelList: List<AreaListDataModelData>,
    private val listener: AreaClickListener
) : RecyclerView.Adapter<AreaListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: AreaListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.area_list_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = areaListDataModelModelList[position]
        holder.bind(dataModel)
        holder.itemRowBinding.areaItem.setOnClickListener { listener.onItemClick(dataModel) }
    }

    override fun getItemCount(): Int {
        return areaListDataModelModelList.size
    }

    inner class ViewHolder(var itemRowBinding: AreaListItemBinding) : RecyclerView.ViewHolder(
        itemRowBinding.root
    ) {
        fun bind(obj: Any?) {
            itemRowBinding.setVariable(BR.area, obj)
            itemRowBinding.executePendingBindings()
        }
    }
}