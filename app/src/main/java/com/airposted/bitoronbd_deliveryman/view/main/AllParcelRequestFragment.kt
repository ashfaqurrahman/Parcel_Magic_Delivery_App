package com.airposted.bitoronbd_deliveryman.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentAllParcelRequestBinding
import com.airposted.bitoronbd_deliveryman.view.auth.adapter.DataAdapter
import com.airposted.bitoronbd_deliveryman.view.model.DataModel
import java.util.ArrayList

class AllParcelRequestFragment : Fragment() {
    private lateinit var binding: FragmentAllParcelRequestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllParcelRequestBinding.inflate(inflater, container, false)
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
        binding.toolbar.toolbarTitle.text = getString(R.string.all_parcel_request)

        val dataList: MutableList<DataModel> = ArrayList<DataModel>()
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        val recyclerView: RecyclerView = binding.allParcelRequestList
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager
        dataList.add(DataModel("Document", "3 kg", "12 cm"))
        dataList.add(DataModel("Product", "5 kg", "12 cm"))
        dataList.add(DataModel("Document", "3 kg", "12 cm"))
        dataList.add(DataModel("Product", "5 kg", "12 cm"))
        val adapter = DataAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}