package com.airposted.bitoronbd_deliveryman.view.main.pending_order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentParcelRequestBinding
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPendingOrderBinding
import com.airposted.bitoronbd_deliveryman.model.LiveOrders
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.OrderClickListener
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.OrderListRecyclerViewAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PendingOrderFragment : Fragment(), KodeinAware, IOnBackPressed {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentPendingOrderBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPendingOrderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.toolbar.toolbarTitle.text = getString(R.string.pending_order)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        var orderListArray = ArrayList<LiveOrders>()
        viewModel.orders.observe(viewLifecycleOwner, {
            orderListArray = it
            val myRecyclerViewAdapter = PendingOrderRecyclerViewAdapter(orderListArray)
            binding.parcelRequestList.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.parcelRequestList.itemAnimator = DefaultItemAnimator()
            binding.parcelRequestList.adapter = myRecyclerViewAdapter
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}