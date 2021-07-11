package com.airposted.bitoronbd_deliveryman.view.main.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.BR
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.WalletListNameBinding
import com.airposted.bitoronbd_deliveryman.model.wallet.WalletDataModel

class WalletHistoryRecyclerViewAdapter(
    private val walletListModelData: List<WalletDataModel>,
//    private val listener: OrderClickListener
) : RecyclerView.Adapter<WalletHistoryRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: WalletListNameBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.wallet_list_name, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = walletListModelData[position]
        holder.bind(dataModel)
//        holder.binding.viewOrder.setOnClickListener { listener.onItemClick(dataModel) }
    }

    override fun getItemCount(): Int {
        return walletListModelData.size
    }

    inner class ViewHolder(var binding: WalletListNameBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.walletHistoryList, obj)
            binding.executePendingBindings()
        }
    }
}