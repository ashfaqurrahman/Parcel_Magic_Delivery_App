package com.airposted.bitoronbd_deliveryman.ui.auth.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.ui.main.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.ui.main.ParcelDetailsFragment
import com.airposted.bitoronbd_deliveryman.ui.model.DataModel
import java.util.*

class DataAdapter(var context: Context, data: List<DataModel>) :
    RecyclerView.Adapter<DataAdapter.HomeViewHolder>() {
    private var data: List<DataModel> = ArrayList<DataModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val theView: View = LayoutInflater.from(context).inflate(R.layout.parcel_request_list_item, parent, false)
        return HomeViewHolder(theView)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        holder.title.text = data[position].title
        holder.size.text = data[position].size
        holder.weight.text = data[position].weight
        holder.parent.setOnClickListener {
            communicatorFragmentInterface.addContentFragment(ParcelDetailsFragment(), true)
        }
    }

    override fun getItemCount(): Int {
        //returns size of the list
        return data.size
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parent: TextView = itemView.findViewById(R.id.view_order)
        val title: TextView = itemView.findViewById(R.id.title)
        val weight: TextView = itemView.findViewById(R.id.weight)
        val size: TextView = itemView.findViewById(R.id.size)
    }

    //item click
    init {
        this.data = data
    }
}