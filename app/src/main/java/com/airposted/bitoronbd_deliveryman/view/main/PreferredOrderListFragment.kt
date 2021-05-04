package com.airposted.bitoronbd_deliveryman.view.main

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
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPreferredOrderListBinding
import com.airposted.bitoronbd_deliveryman.model.PreferredAreaOrderListModel
import com.airposted.bitoronbd_deliveryman.model.PreferredAreaOrderListModelData
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PreferredOrderListFragment : Fragment(), KodeinAware, PreferredOrderClickListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentPreferredOrderListBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferredOrderListBinding.inflate(inflater, container, false)
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
            binding.preferredArea.dismiss()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.preferred_area_order_list)

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getPreferredOrderList()
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

    private fun showOrderList(response: PreferredAreaOrderListModel) {
        if (response.data.isNotEmpty()) {
            binding.allParcelRequestList.visibility = View.VISIBLE
            binding.noOrder.visibility = View.GONE
            val myRecyclerViewAdapter = PreferredOrderListRecyclerViewAdapter(response.data, this)
            binding.allParcelRequestList.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.allParcelRequestList.itemAnimator = DefaultItemAnimator()
            binding.allParcelRequestList.adapter = myRecyclerViewAdapter
            dismissDialog()
        }
        else {
            binding.allParcelRequestList.visibility = View.GONE
            binding.noOrder.visibility = View.VISIBLE
            dismissDialog()
        }
    }

    override fun onItemClick(preferredOrder: PreferredAreaOrderListModelData) {
        val fragment = ParcelDetailsFragment()
        val bundle = Bundle()
        bundle.putString("delivery_date", preferredOrder.delivery_date)
        bundle.putString("pick_address", preferredOrder.pick_address)
        bundle.putString("recp_address", preferredOrder.recp_address)
        bundle.putString("order_item_name", preferredOrder.order_item_name)
        bundle.putString("invoice", preferredOrder.invoice_no)
        fragment.arguments = bundle
        communicatorFragmentInterface?.addContentFragment(fragment, true)
    }

}