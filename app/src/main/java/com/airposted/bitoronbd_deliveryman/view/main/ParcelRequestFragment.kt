package com.airposted.bitoronbd_deliveryman.view.main

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
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ParcelRequestFragment : Fragment(), KodeinAware, OrderClickListener {
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
        BindUI()
    }

    private fun BindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        binding.toolbar.toolbarTitle.text = getString(R.string.parcel_request)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
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
        bundle.putInt("id", order.id)
        fragment.arguments = bundle
        communicatorFragmentInterface?.addContentFragment(fragment, true)
    }
}