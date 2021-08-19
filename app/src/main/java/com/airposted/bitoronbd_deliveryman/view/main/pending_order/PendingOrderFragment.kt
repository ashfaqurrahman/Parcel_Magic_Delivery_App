package com.airposted.bitoronbd_deliveryman.view.main.pending_order

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.MalformedJsonException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.data.network.preferences.PreferenceProvider
import com.airposted.bitoronbd_deliveryman.databinding.FragmentParcelRequestBinding
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPendingOrderBinding
import com.airposted.bitoronbd_deliveryman.model.LiveOrders
import com.airposted.bitoronbd_deliveryman.utils.NoInternetException
import com.airposted.bitoronbd_deliveryman.utils.dismissDialog
import com.airposted.bitoronbd_deliveryman.utils.setProgressDialog
import com.airposted.bitoronbd_deliveryman.utils.snackbar
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import com.airposted.bitoronbd_deliveryman.view.main.live_parcel.MyLiveDeliveryFragment
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.OrderClickListener
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.OrderListRecyclerViewAdapter
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.ParcelRequestFragment
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PendingOrderFragment(private val orderListArray: ArrayList<LiveOrders>) : Fragment(), PendingOrderClickListener, KodeinAware, IOnBackPressed {

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
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        binding.toolbar.toolbarTitle.text = getString(R.string.pending_order)
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        val myRecyclerViewAdapter = PendingOrderRecyclerViewAdapter(orderListArray, this)
        binding.parcelRequestList.layoutManager = GridLayoutManager(requireActivity(), 1)
        binding.parcelRequestList.itemAnimator = DefaultItemAnimator()
        binding.parcelRequestList.adapter = myRecyclerViewAdapter
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onItemClick(liveOrders: LiveOrders) {
        val dialogs = Dialog(requireActivity())
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.order_accept_dialog)
        dialogs.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogs.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,  //w
            ViewGroup.LayoutParams.MATCH_PARENT //h
        )

        val cancel = dialogs.findViewById<TextView>(R.id.cancel)
        val ok = dialogs.findViewById<TextView>(R.id.ok)
        cancel.setOnClickListener {
            dialogs.dismiss()
        }

        ok.setOnClickListener {
            dialogs.dismiss()
            setProgressDialog(requireActivity())
            lifecycleScope.launch {
                try {
                    val response = viewModel.changeStatus(liveOrders.invoice_no, 3, 0)
                    if (response.success) {
                        dismissDialog()
                        requireActivity().supportFragmentManager.popBackStack(
                            PendingOrderFragment::class.java.name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        communicatorFragmentInterface!!.addContentFragment(
                            MyLiveDeliveryFragment(), true)
                    }
                    else {
                        binding.rootLayout.snackbar(response.msg)
                        dismissDialog()
                    }
                } catch (e: JsonSyntaxException) {
                    dismissDialog()
                    binding.rootLayout.snackbar(e.message!!)
                    e.printStackTrace()
                } catch (e: MalformedJsonException) {
                    dismissDialog()
                    binding.rootLayout.snackbar(e.message!!)
                    e.printStackTrace()
                } catch (e: com.airposted.bitoronbd_deliveryman.utils.ApiException) {
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

        dialogs.setCancelable(true)
        dialogs.show()
    }

}