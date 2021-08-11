package com.airposted.bitoronbd_deliveryman.view.main.all_orders

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
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentAllOrdersBinding
import com.airposted.bitoronbd_deliveryman.model.all_orders.AllOrders
import com.airposted.bitoronbd_deliveryman.model.all_orders.Data
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.ParcelDetailsFragment
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AllOrdersFragment : Fragment(), KodeinAware, AllOrderClickListener, IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentAllOrdersBinding
    private var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllOrdersBinding.inflate(inflater, container, false)
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
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.all_orders)

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getAllOrders()
                if (response.data != null) {
                    showAllArea(response)
                }
            } catch (e: JsonSyntaxException) {
                dismissDialog()
                binding.rootLayout.snackbar(e.message!!)
                e.printStackTrace()
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

    private fun showAllArea(response: AllOrders) {
        if (response.data.isNotEmpty()) {
            binding.allOrdersRecycler.visibility = View.VISIBLE
            binding.myNoArea.visibility = View.GONE
            val myRecyclerViewAdapter = AllOrderListRecyclerViewAdapter(response.data, this)
            binding.allOrdersRecycler.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.allOrdersRecycler.itemAnimator = DefaultItemAnimator()
            binding.allOrdersRecycler.adapter = myRecyclerViewAdapter
            dismissDialog()
        }
        else {
            binding.allOrdersRecycler.visibility = View.GONE
            binding.myNoArea.visibility = View.VISIBLE
            dismissDialog()
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onItemClick(data: Data) {
        val fragment = ParcelDetailsFragment()
        val bundle = Bundle()
        bundle.putString("delivery_date", data.delivery_date)
        bundle.putString("pick_address", data.pick_address)
        bundle.putString("recp_address", data.recp_address)
        bundle.putString("order_item_name", data.order_item_name)
        bundle.putDouble("distance", data.distance)
        bundle.putDouble("delivery_charge", data.delivery_charge)
        bundle.putInt("item_type", data.item_type)
        bundle.putInt("item_qty", data.item_qty)
        bundle.putString("invoice", data.invoice_no)
        fragment.arguments = bundle
        communicatorFragmentInterface?.addContentFragment(fragment, true)
    }

}