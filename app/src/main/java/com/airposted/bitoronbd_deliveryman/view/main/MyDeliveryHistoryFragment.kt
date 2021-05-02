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
import com.airposted.bitoronbd_deliveryman.databinding.FragmentMyDeliveryHistoryBinding
import com.airposted.bitoronbd_deliveryman.model.OrderListModel
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MyDeliveryHistoryFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentMyDeliveryHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyDeliveryHistoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.my_delivery_history)

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.myOrderHistory()
                if (response.success) {
                    showOrderHistory(response)
                }
                dismissDialog()
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

    private fun showOrderHistory(response: OrderListModel) {
        if (response.data.isNotEmpty()) {
            binding.myOrderHistoryList.visibility = View.VISIBLE
            binding.noOrder.visibility = View.GONE
            val myRecyclerViewAdapter = OrderHistoryRecyclerViewAdapter(response.data)
            binding.myOrderHistoryList.layoutManager = GridLayoutManager(requireActivity(), 2)
            binding.myOrderHistoryList.itemAnimator = DefaultItemAnimator()
            binding.myOrderHistoryList.adapter = myRecyclerViewAdapter
            dismissDialog()
        }
        else {
            binding.myOrderHistoryList.visibility = View.GONE
            binding.noOrder.visibility = View.VISIBLE
        }
    }
}