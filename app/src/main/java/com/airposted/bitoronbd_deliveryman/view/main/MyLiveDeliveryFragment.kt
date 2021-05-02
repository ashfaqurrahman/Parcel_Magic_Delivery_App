package com.airposted.bitoronbd_deliveryman.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.MalformedJsonException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.airposted.bitoronbd_deliveryman.databinding.FragmentMyLiveDeliveryBinding
import com.airposted.bitoronbd_deliveryman.model.OrderListModel
import com.airposted.bitoronbd_deliveryman.model.OrderListModelData
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MyLiveDeliveryFragment : Fragment(), KodeinAware, CurrentOrderClickListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentMyLiveDeliveryBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyLiveDeliveryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
            binding.deliveryType.dismiss()
        }
        binding.toolbar.toolbarTitle.text = "My Live Delivery"

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getCurrentOrderList()
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
            binding.myLiveDeliveryList.visibility = View.VISIBLE
            binding.noOrder.visibility = View.GONE
            val myRecyclerViewAdapter = CurrentOrderListRecyclerViewAdapter(response.data, this)
            binding.myLiveDeliveryList.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.myLiveDeliveryList.itemAnimator = DefaultItemAnimator()
            binding.myLiveDeliveryList.adapter = myRecyclerViewAdapter
            dismissDialog()
        }
        else {
            binding.myLiveDeliveryList.visibility = View.GONE
            binding.noOrder.visibility = View.VISIBLE
            dismissDialog()
        }
    }

    override fun onItemClick(currentOrder: OrderListModelData) {
        val fragment = LiveParcelDetailsFragment()
        val bundle = Bundle()
        bundle.putString("delivery_date", currentOrder.delivery_date)
        bundle.putString("pick_address", currentOrder.pick_address)
        bundle.putString("recp_address", currentOrder.recp_address)
        bundle.putString("recp_name", currentOrder.recp_name)
        bundle.putString("recp_phone", currentOrder.recp_phone)
        bundle.putString("sender_name", currentOrder.recp_name)
        bundle.putString("sender_phone", currentOrder.recp_phone)
        bundle.putDouble("distance", currentOrder.distance)
        bundle.putInt("current_status", currentOrder.current_status)
        bundle.putInt("coc", currentOrder.coc)
        bundle.putInt("cod", currentOrder.cod)
        bundle.putDouble("price", 120.50)
        bundle.putString("invoice", currentOrder.invoice_no)
        fragment.arguments = bundle
        communicatorFragmentInterface?.addContentFragment(fragment, true)
    }

    override fun onCallClick(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        startActivity(intent)
    }

}