package com.airposted.bitoronbd_deliveryman.view.main.preferred_area

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.MalformedJsonException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentPreferredAreaBinding
import com.airposted.bitoronbd_deliveryman.model.AreaListDataModelData
import com.airposted.bitoronbd_deliveryman.model.ViewMyAreaModel
import com.airposted.bitoronbd_deliveryman.model.ViewMyAreaModelData
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.main.*
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.ParcelRequestFragment
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PreferredAreaFragment : Fragment(), KodeinAware, AreaClickListener, MyAreaClickListener,
    IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var areaList: List<AreaListDataModelData>
    private lateinit var dialogs: Dialog
    private lateinit var binding: FragmentPreferredAreaBinding
    private var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferredAreaBinding.inflate(inflater, container, false)
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
        binding.toolbar.toolbarTitle.text = getString(R.string.preferred_area)

        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val allAreaResponse = viewModel.getAllAreaList()
                val myAreaResponse = viewModel.viewMyArea()
                areaList = allAreaResponse.data!!
                if (allAreaResponse.success && myAreaResponse.success) {
                    showMyArea(myAreaResponse)
                }
                else {
                    binding.rootLayout.snackbar(myAreaResponse.msg)
                    dismissDialog()
                }
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

        binding.addAreaBtn.setOnClickListener {
            searchArea()
        }
    }

    private fun searchArea() {
        dialogs = Dialog(requireActivity())
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.search_area_dialog)
        dialogs.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogs.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,  //w
            ViewGroup.LayoutParams.MATCH_PARENT //h
        )

        val back = dialogs.findViewById<ImageView>(R.id.backImage)
        val areaRecycler = dialogs.findViewById<RecyclerView>(R.id.area_recycler)
        val noArea = dialogs.findViewById<TextView>(R.id.no_area)
        val search_item = dialogs.findViewById<EditText>(R.id.search_item)
        back.setOnClickListener {
            dialogs.dismiss()
        }

        search_item.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                areaRecycler.layoutManager = GridLayoutManager(
                    requireActivity(),
                    1
                )
                areaRecycler.itemAnimator = DefaultItemAnimator()
                if (s.toString().isNotEmpty()) {
                    val listNew: ArrayList<AreaListDataModelData> = ArrayList()
                    for (l in areaList.indices) {
                        val serviceName: String = areaList[l].area_name
                        if (serviceName.toLowerCase().contains(s.toString().toLowerCase())) {
                            listNew.add(areaList[l])
                            areaRecycler.visibility = View.VISIBLE
                            noArea.visibility = View.GONE
                        }
                    }
                    if (listNew.isNullOrEmpty()) {
                        areaRecycler.visibility = View.GONE
                        noArea.visibility = View.VISIBLE
                    } else {
                        val myRecyclerViewAdapter = AreaListRecyclerViewAdapter(
                            listNew,
                            this@PreferredAreaFragment
                        )
                        areaRecycler.adapter = myRecyclerViewAdapter
                    }
                } else {
                    areaRecycler.visibility = View.VISIBLE
                    noArea.visibility = View.GONE
                    val myRecyclerViewAdapter = AreaListRecyclerViewAdapter(
                        areaList,
                        this@PreferredAreaFragment
                    )
                    areaRecycler.adapter = myRecyclerViewAdapter
                }
            }
        })

        if (areaList.isNotEmpty()) {
            areaRecycler.visibility = View.VISIBLE
            noArea.visibility = View.GONE
            val myRecyclerViewAdapter = AreaListRecyclerViewAdapter(areaList, this)
            areaRecycler.layoutManager = GridLayoutManager(requireActivity(), 1)
            areaRecycler.itemAnimator = DefaultItemAnimator()
            areaRecycler.adapter = myRecyclerViewAdapter
        }
        else {
            areaRecycler.visibility = View.GONE
            noArea.visibility = View.VISIBLE
        }

//        dialogs.window?.attributes?.windowAnimations = R.style.DialogAnimation_2
//        dialogs.window?.attributes?.gravity = Gravity.BOTTOM

        dialogs.setCancelable(false)

        dialogs.show()
    }

    override fun onItemClick(area: AreaListDataModelData) {
        dialogs.dismiss()
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.addMyArea(area.id)
                if (response.success) {
                    FirebaseMessaging.getInstance().subscribeToTopic(area.area_name)
                        .addOnCompleteListener { task ->
                            var msg = getString(R.string.open)
                            if (!task.isSuccessful) {
                                msg = getString(R.string.close)
                            }
                            //Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    binding.rootLayout.snackbar(response.msg)
                    syncMyAreaList()
                }
                else {
                    binding.rootLayout.snackbar(response.msg)
                    dismissDialog()
                }
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

    private fun syncMyAreaList() {
        lifecycleScope.launch {
            try {
                val response = viewModel.viewMyArea()
                if (response.success) {
                    showMyArea(response)
                }
                else {
                    binding.rootLayout.snackbar(response.msg)
                    dismissDialog()
                }
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

    private fun showMyArea(myAreaResponse: ViewMyAreaModel) {
        if (myAreaResponse.data.isNotEmpty()) {
            binding.myReaRecycler.visibility = View.VISIBLE
            binding.myNoArea.visibility = View.GONE
            val myRecyclerViewAdapter = MyAreaListRecyclerViewAdapter(myAreaResponse.data, this)
            binding.myReaRecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
            binding.myReaRecycler.itemAnimator = DefaultItemAnimator()
            binding.myReaRecycler.adapter = myRecyclerViewAdapter
            dismissDialog()
        }
        else {
            binding.myReaRecycler.visibility = View.GONE
            binding.myNoArea.visibility = View.VISIBLE
            dismissDialog()
        }
    }

    override fun onItemClickListener(area: ViewMyAreaModelData) {
        val fragment = ParcelRequestFragment()
        val bundle = Bundle()
        bundle.putString("context", "preferred")
        bundle.putInt("areaId", area.area_id)
        fragment.arguments = bundle
        communicatorFragmentInterface!!.addContentFragment(fragment, true)
    }

    override fun onItemDeleteListener(area: ViewMyAreaModelData) {
        val dialogs = Dialog(requireActivity())
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.delete_dialog)
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
                    val response = viewModel.deleteMyArea(area.id)
                    if (response.success) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(area.area_name)
                        binding.rootLayout.snackbar(response.msg)
                        syncMyAreaList()
                    }
                    else {
                        binding.rootLayout.snackbar(response.msg)
                        dismissDialog()
                    }
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

        dialogs.setCancelable(false)
        dialogs.show()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}