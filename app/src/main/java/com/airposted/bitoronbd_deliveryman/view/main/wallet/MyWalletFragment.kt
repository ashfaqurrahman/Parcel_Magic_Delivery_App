package com.airposted.bitoronbd_deliveryman.view.main.wallet

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
import com.airposted.bitoronbd_deliveryman.databinding.FragmentMyWalletBinding
import com.airposted.bitoronbd_deliveryman.model.WalletModel
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.HomeViewModelFactory
import com.airposted.bitoronbd_deliveryman.view.main.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.history.OrderHistoryRecyclerViewAdapter
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MyWalletFragment : Fragment(), KodeinAware, IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentMyWalletBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyWalletBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BindUI()
    }

    private fun BindUI() {
        binding.customToolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.customToolbar.toolbarTitle.text = "My Wallet"

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.myWallet()
                if (response.success) {
                    binding.totalEarning.text = "৳" + response.wallet_info.total_amount.toString()
                    binding.monthlyIncome.text = "৳" + response.wallet_info.monthly_income.toString()
                    binding.dueAmount.text = "৳" + response.wallet_info.due_amount.toString()
                    showWalletHistory(response)
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

    private fun showWalletHistory(response: WalletModel) {
        if (response.data.data.isNotEmpty()) {
            binding.myWalletHistoryList.visibility = View.VISIBLE
            binding.noOrder.visibility = View.GONE
            val myRecyclerViewAdapter = WalletHistoryRecyclerViewAdapter(response.data.data)
            binding.myWalletHistoryList.layoutManager = GridLayoutManager(requireActivity(), 1)
            binding.myWalletHistoryList.itemAnimator = DefaultItemAnimator()
            binding.myWalletHistoryList.adapter = myRecyclerViewAdapter
            dismissDialog()
        }
        else {
            binding.myWalletHistoryList.visibility = View.GONE
            binding.noOrder.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}


