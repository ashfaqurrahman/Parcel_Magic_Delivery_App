package com.airposted.bitoronbd_deliveryman.view.main.preferred_order

import android.os.Bundle
import android.util.MalformedJsonException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPreferredOrderListBinding
import com.airposted.bitoronbd_deliveryman.model.*
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.*
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


class PreferredOrderListFragment : Fragment(), KodeinAware, PreferredOrderClickListener,
    IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentPreferredOrderListBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    private lateinit var areaList: List<ViewMyAreaModelData>
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
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.preferred_area_order_list)

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                dismissDialog()
                val myAreaResponse = viewModel.viewMyArea()
                areaList = myAreaResponse.data
                val array = Array<String?>(areaList.size) { null }
                for (i in areaList.indices) {
                    array[i] = areaList[i].area_name
                }
                val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, array)
                binding.spinner.adapter = arrayAdapter
                binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {

                        for(i in areaList.indices) {
                            if (areaList[i].area_name == parent.selectedItem.toString()){
                                //binding.rootLayout.snackbar(areaList[0].area_id.toString())
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
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

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getPreferredOrderList()
                showOrderList(response)
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
        bundle.putDouble("distance", preferredOrder.distance)
        bundle.putDouble("delivery_charge", preferredOrder.delivery_charge)
        bundle.putInt("item_type", preferredOrder.item_type)
        bundle.putInt("item_qty", preferredOrder.item_qty)
        bundle.putString("invoice", preferredOrder.invoice_no)
        fragment.arguments = bundle
        communicatorFragmentInterface?.addContentFragment(fragment, true)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}