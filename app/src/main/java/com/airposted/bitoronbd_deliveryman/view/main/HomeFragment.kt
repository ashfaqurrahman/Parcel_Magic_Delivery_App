package com.airposted.bitoronbd_deliveryman.view.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.MalformedJsonException
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.BuildConfig
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentHomeBinding
import com.airposted.bitoronbd_deliveryman.model.DataX
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.auth.AuthActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener, KodeinAware, CustomClickListener {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentHomeBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var invoice: List<DataX>
    private var currentLocationClick = false
    private lateinit var dialogs: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        Log.e("AAAA", PersistentUser.getInstance().getAccessToken(requireActivity()))
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getAllAreaList()
                invoice = response.data!!
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

        binding.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.versionName.text = "Version " + BuildConfig.VERSION_NAME
        communicatorFragmentInterface = context as CommunicatorFragmentInterface

        binding.from.setOnClickListener {
            currentLocationClick = true
            searchArea()
        }

        binding.to.setOnClickListener {
            currentLocationClick = false
            searchArea()
        }

        binding.getOrderRequest.setOnClickListener {
            if (binding.from.text.toString().isNotEmpty() && binding.from.text.toString()
                    .isNotEmpty()
            ) {
                val fragment = ParcelRequestFragment()
                val bundle = Bundle()
                bundle.putString("from", "")
                fragment.arguments = bundle
                communicatorFragmentInterface?.addContentFragment(fragment, true)
            }
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
                    val listNew: ArrayList<DataX> = ArrayList()
                    for (l in invoice.indices) {
                        val serviceName: String = invoice[l].area_name
                        if (serviceName.toLowerCase().contains(s.toString().toLowerCase())) {
                            listNew.add(invoice[l])
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
                            this@HomeFragment
                        )
                        areaRecycler.adapter = myRecyclerViewAdapter
                    }
                } else {
                    areaRecycler.visibility = View.VISIBLE
                    noArea.visibility = View.GONE
                    val myRecyclerViewAdapter = AreaListRecyclerViewAdapter(
                        invoice,
                        this@HomeFragment
                    )
                    areaRecycler.adapter = myRecyclerViewAdapter
                }
            }
        })

        if (invoice.isNotEmpty()) {
            areaRecycler.visibility = View.VISIBLE
            noArea.visibility = View.GONE
            val myRecyclerViewAdapter = AreaListRecyclerViewAdapter(invoice, this)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.my_wallet -> {
//                communicatorFragmentInterface?.addContentFragment(MyWalletFragment(), true)
//                binding.drawerLayout.closeDrawers()
//            }
            R.id.my_live_delivery -> {
                communicatorFragmentInterface?.addContentFragment(
                    MyLiveDeliveryFragment(),
                    true
                )
                binding.drawerLayout.closeDrawers()
            }
            R.id.all_parcel_request -> {
                communicatorFragmentInterface?.addContentFragment(
                    AllParcelRequestFragment(),
                    true
                )
                binding.drawerLayout.closeDrawers()
            }
            R.id.preferred_area -> {
                communicatorFragmentInterface?.addContentFragment(PreferredAreaFragment(), true)
                binding.drawerLayout.closeDrawers()
            }
            R.id.my_delivery_history -> {
                communicatorFragmentInterface?.addContentFragment(
                    MyDeliveryHistoryFragment(),
                    true
                )
                binding.drawerLayout.closeDrawers()
            }
            R.id.profile -> {
                communicatorFragmentInterface?.addContentFragment(ProfileFragment(), true)
                binding.drawerLayout.closeDrawers()
            }
            R.id.help -> {
                binding.drawerLayout.closeDrawers()
            }
            R.id.settings -> {
                binding.drawerLayout.closeDrawers()
            }
            R.id.terms_condition -> {
                binding.drawerLayout.closeDrawers()
                val fragment = WebViewFragment()
                val bundle = Bundle()
                bundle.putString(AppHelper.DETAILS_KEY, AppHelper.TERMS)
                fragment.arguments = bundle
                communicatorFragmentInterface?.addContentFragment(fragment, true)
            }
            R.id.sign_out -> {
                PersistentUser.getInstance().logOut(requireActivity())
                binding.drawerLayout.closeDrawers()
                val intent = Intent(requireActivity(), AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        return true
    }

    override fun onItemClick(area: DataX) {
        dialogs.dismiss()
        if (currentLocationClick) {
            binding.from.text = area.area_name
        }
        else {
            binding.to.text = area.area_name
        }
    }
}