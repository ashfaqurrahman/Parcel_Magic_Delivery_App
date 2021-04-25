package com.airposted.bitoronbd_deliveryman.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentParcelRequestBinding
import com.airposted.bitoronbd_deliveryman.view.auth.adapter.DataAdapter
import com.airposted.bitoronbd_deliveryman.view.model.DataModel
import java.util.*

class ParcelRequestFragment : Fragment() {
    private lateinit var binding: FragmentParcelRequestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParcelRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BindUI()
    }

    private fun BindUI() {
        binding.toolbar.toolbarTitle.text = getString(R.string.parcel_request)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.from.text = requireArguments().getString("from")
        binding.to.text = requireArguments().getString("to")

        val dataList: MutableList<DataModel> = ArrayList<DataModel>()
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        val recyclerView: RecyclerView = binding.parcelRequestList
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