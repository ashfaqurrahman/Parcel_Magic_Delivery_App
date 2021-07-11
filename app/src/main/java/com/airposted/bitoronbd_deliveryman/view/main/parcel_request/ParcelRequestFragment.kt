package com.airposted.bitoronbd_deliveryman.view.main.parcel_request

import android.os.Bundle
import android.util.MalformedJsonException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentParcelRequestBinding
import com.airposted.bitoronbd_deliveryman.model.OrderListModel
import com.airposted.bitoronbd_deliveryman.model.OrderListModelData
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ParcelRequestFragment : Fragment(), KodeinAware, OrderClickListener, IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentParcelRequestBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParcelRequestBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        binding.toolbar.toolbarTitle.text = getString(R.string.parcel_request)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        when {
            requireArguments().getString("context") == "home" -> {
                searchByFromTo()
            }
            requireArguments().getString("context") == "preferred" -> {
                binding.layout.visibility = View.GONE
                searchByTo(requireArguments().getInt("areaId"))
            }
            else -> {
                binding.rootLayout.snackbar("Null context!!")
            }
        }

    }

    private fun searchByTo(id: Int) {
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getOrderListByArea(id)
                showOrderList(response)
            } catch (e: MalformedJsonException) {
                dismissDialog()
                binding.rootLayout.snackbar(e.message!!)
                e.printStackTrace()
            } catch (e: ApiException) {
                dismissDialog()
                binding.rootLayout.snackbar(e.message!!)
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissDialog()
                binding.rootLayout.snackbar(e.message!!)
                e.printStackTrace()
            }
        }
    }

    private fun searchByFromTo() {
        binding.from.text = requireArguments().getString("from")
        binding.to.text = requireArguments().getString("to")
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getOrderList(requireArguments().getInt("toId"), requireArguments().getInt("fromId"))
                showOrderList(response)
            } catch (e: MalformedJsonException) {
                dismissDialog()
                binding.rootLayout.snackbar(e.message!!)
                e.printStackTrace()
            } catch (e: ApiException) {
                dismissDialog()
                binding.rootLayout.snackbar(e.message!!)
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissDialog()
                binding.rootLayout.snackbar(e.message!!)
                e.printStackTrace()
            }
        }
    }

    private fun showOrderList(response: OrderListModel) {
        if (response.data.isNotEmpty()) {
            binding.parcelRequestList.visibility = View.VISIBLE
            binding.noOrder.visibility = View.GONE
            val myRecyclerViewAdapter = OrderListRecyclerViewAdapter(response.data, this)
            binding.parcelRequestList.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.parcelRequestList.itemAnimator = DefaultItemAnimator()
            binding.parcelRequestList.adapter = myRecyclerViewAdapter
        }
        else {
            binding.parcelRequestList.visibility = View.GONE
            binding.noOrder.visibility = View.VISIBLE
        }
        dismissDialog()
    }

    override fun onItemClick(order: OrderListModelData) {
        val fragment = ParcelDetailsFragment()
        val bundle = Bundle()
        bundle.putString("delivery_date", order.delivery_date)
        bundle.putString("pick_address", order.pick_address)
        bundle.putString("recp_address", order.recp_address)
        bundle.putString("order_item_name", order.order_item_name)
        bundle.putDouble("distance", order.distance)
        bundle.putDouble("delivery_charge", order.delivery_charge)
        bundle.putInt("item_type", order.item_type)
        bundle.putInt("item_qty", order.item_qty)
        bundle.putString("invoice", order.invoice_no)
        fragment.arguments = bundle
        communicatorFragmentInterface?.addContentFragment(fragment, true)
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}