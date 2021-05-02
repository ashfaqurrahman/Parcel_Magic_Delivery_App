package com.airposted.bitoronbd_deliveryman.view.main

import android.os.Bundle
import android.util.MalformedJsonException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.databinding.FragmentProfileBinding
import com.airposted.bitoronbd_deliveryman.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProfileFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
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

        binding.name.text = PersistentUser.getInstance().getFullName(requireActivity())

//        setProgressDialog(requireActivity())
//        lifecycleScope.launch {
//            try {
//                val response = viewModel.myProfile()
//
//            } catch (e: MalformedJsonException) {
//                dismissDialog()
//                binding.rootLayout.snackbar(e.message!!)
//                e.printStackTrace()
//            } catch (e: ApiException) {
//                dismissDialog()
//                binding.rootLayout.snackbar(e.message!!)
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                dismissDialog()
//                binding.rootLayout.snackbar(e.message!!)
//                e.printStackTrace()
//            }
//
//        }
    }
}