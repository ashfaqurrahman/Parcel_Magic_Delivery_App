package com.airposted.bitoronbd_deliveryman.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentMyLiveDeliveryBinding
import com.airposted.bitoronbd_deliveryman.ui.auth.adapter.DataAdapter
import com.airposted.bitoronbd_deliveryman.ui.auth.adapter.LiveDeliveryAdapter
import com.airposted.bitoronbd_deliveryman.ui.model.DataModel
import java.util.ArrayList

class MyLiveDeliveryFragment : Fragment() {
    private lateinit var binding:FragmentMyLiveDeliveryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyLiveDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BindUI()
    }

    private fun BindUI() {
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.toolbarTitle.text = "My Live Delivery"

        val dataList: MutableList<DataModel> = ArrayList<DataModel>()
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        val recyclerView: RecyclerView = binding.myLiveDeliveryList
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager
        dataList.add(DataModel("Document", "3 kg", "12 cm"))
        dataList.add(DataModel("Product", "5 kg", "12 cm"))
        dataList.add(DataModel("Document", "3 kg", "12 cm"))
        dataList.add(DataModel("Product", "5 kg", "12 cm"))
        val adapter = LiveDeliveryAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}