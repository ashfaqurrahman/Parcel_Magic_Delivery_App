package com.airposted.bitoronbd_deliveryman.view.main.home

import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.BuildConfig
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.data.network.preferences.PreferenceProvider
import com.airposted.bitoronbd_deliveryman.databinding.FragmentHomeBinding
import com.airposted.bitoronbd_deliveryman.model.AreaListDataModelData
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.auth.AuthActivity
import com.airposted.bitoronbd_deliveryman.view.main.*
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.history.MyDeliveryHistoryFragment
import com.airposted.bitoronbd_deliveryman.view.main.live_parcel.MyLiveDeliveryFragment
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.ParcelRequestFragment
import com.airposted.bitoronbd_deliveryman.view.main.preferred_area.AreaClickListener
import com.airposted.bitoronbd_deliveryman.view.main.preferred_area.AreaListRecyclerViewAdapter
import com.airposted.bitoronbd_deliveryman.view.main.preferred_area.PreferredAreaFragment
import com.airposted.bitoronbd_deliveryman.view.main.wallet.MyWalletFragment
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonSyntaxException
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList
import com.airposted.bitoronbd_deliveryman.view.main.MainActivity

import com.airposted.bitoronbd_deliveryman.view.OrderRequestFragment
import com.google.firebase.database.*
import android.widget.CompoundButton
import com.airposted.bitoronbd_deliveryman.model.LiveOrders
import com.airposted.bitoronbd_deliveryman.view.main.pending_order.PendingOrderFragment


class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener, KodeinAware,
    AreaClickListener, IOnBackPressed {
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    private lateinit var areaList: List<AreaListDataModelData>
    private lateinit var dialogs: Dialog
    private var currentLocationClick = false
    private var fromId = 0
    private var toID = 0
    private var from = ""
    private var to = ""

    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
    private val REQUEST_CHECK_SETTINGS = 0x1
    private lateinit var googleApiClient: GoogleApiClient
    private var gpsDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        gpsDialog = Dialog(requireActivity())
        setProgressDialog(requireActivity())

        googleApiClient = getAPIClientInstance()
        googleApiClient.connect()

        val manager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (statusOfGPS) {
            viewModel.startLocation
        }

        viewModel.gps.observe( viewLifecycleOwner, {
            if (it) {
                if (gpsDialog!!.isShowing) {
                    gpsDialog!!.dismiss()
                }
                val location = PreferenceProvider(requireActivity()).getSharedPreferences("location")
                val latitude = PreferenceProvider(requireActivity()).getSharedPreferences("latitude")
                val longitude = PreferenceProvider(requireActivity()).getSharedPreferences("longitude")

                if (!latitude.isNullOrEmpty() && !longitude.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        try {
                            val resources = viewModel.getMyCurrentArea()
                            if (resources.data != null) {
                                binding.from.setText(resources.data.area_name)
                                PreferenceProvider(requireActivity()).saveSharedPreferences("area_id", resources.data.id.toString())
                                from = resources.data.area_name
                                dismissDialog()
                            } else {
                                dismissDialog()
                                binding.rootLayout.snackbar(resources.msg)
                            }
                        } catch (e: JsonSyntaxException) {
                            dismissDialog()
                            binding.rootLayout.snackbar(e.message!!)
                            e.printStackTrace()
                        } catch (e: com.google.gson.stream.MalformedJsonException) {
                            dismissDialog()
                            binding.rootLayout.snackbar(e.message!!)
                            e.printStackTrace()
                        } catch (e: com.google.android.gms.common.api.ApiException) {
                            dismissDialog()
                            binding.rootLayout.snackbar(e.message!!)
                            e.printStackTrace()
                        }catch (e: ApiException) {
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
            } else {
                turnOnGPS()
            }
        })

        val hView: View = binding.navigationView.getHeaderView(0)
        val pic = hView.findViewById<CircleImageView>(R.id.profile_image)
        val name = hView.findViewById<TextView>(R.id.user_name)

        viewModel.name.observe(viewLifecycleOwner) {
            name.text = it
        }

        viewModel.image.observe(viewLifecycleOwner) {
            Glide.with(requireActivity()).load(
                it
            ).placeholder(R.mipmap.ic_launcher).error(
                R.drawable.sample_pro_pic
            ).into(pic)
        }

        lifecycleScope.launch {
            try {
                val response = viewModel.getAllAreaList()
                val myAreaResponse = viewModel.viewMyArea()
                if (myAreaResponse.data.isNotEmpty()){
                    for (i in myAreaResponse.data.indices){
                        FirebaseMessaging.getInstance().subscribeToTopic(myAreaResponse.data[i].area_name)
                            .addOnCompleteListener { task ->

                                /*if (!task.isSuccessful) {

                                }*/
                                //Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                areaList = response.data!!
                FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener { instanceIdResult ->
                    val token = instanceIdResult.token
                    lifecycleScope.launch {
                        try {
                            val saveFcmTokenResponse = viewModel.saveFcmToken(token)
                            if (saveFcmTokenResponse.success) {
                                //dismissDialog()
                            }
                        } catch (e: JsonSyntaxException) {
                            dismissDialog()
                            binding.rootLayout.snackbar(e.message!!)
                            e.printStackTrace()
                        } catch (e: com.google.gson.stream.MalformedJsonException) {
                            dismissDialog()
                            binding.rootLayout.snackbar(e.message!!)
                            e.printStackTrace()
                        } catch (e: com.google.android.gms.common.api.ApiException) {
                            dismissDialog()
                            binding.rootLayout.snackbar(e.message!!)
                            e.printStackTrace()
                        }catch (e: ApiException) {
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
            } catch (e: com.google.gson.stream.MalformedJsonException) {
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

        val orderListArray = ArrayList<LiveOrders>()

        binding.pendingOrders.setOnClickListener {
            if (orderListArray.size > 0) {
                communicatorFragmentInterface!!.addContentFragment(PendingOrderFragment(orderListArray), true)
            }
        }

        binding.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.versionName.text = "Version " + BuildConfig.VERSION_NAME
        communicatorFragmentInterface = context as CommunicatorFragmentInterface

        /*binding.from.setOnClickListener {
            currentLocationClick = true
            searchArea()
        }*/

        binding.to.setOnClickListener {
            currentLocationClick = false
            searchArea()
        }

        binding.onlineOffline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.onlineOffline.text = "You're Online"
                viewModel.orders.observe(viewLifecycleOwner, {
                    for (i in 0 until it.size) {
                        if (it[i].sender_area == PreferenceProvider(requireActivity()).getSharedPreferences("area_id")?.toInt()) {
                            orderListArray.add(it[i])
                        }
                    }
                    binding.counter.text = orderListArray.size.toString()
                    binding.pendingOrders.visibility = View.VISIBLE
                })

                var status = 0
                viewModel.order.observe(viewLifecycleOwner, {
                    if (status == 1) {
                        if (it[0].sender_area == PreferenceProvider(requireActivity()).getSharedPreferences(
                                "area_id"
                            )!!.toInt()
                        ) {
                            val fragment = OrderRequestFragment()
                            val bundle = Bundle()
                            bundle.putString("invoice", it[0].invoice_no)
                            bundle.putString("sender_address", it[0].sender_address)
                            bundle.putString("receiver_address", it[0].receiver_address)
                            bundle.putDouble("sender_lat", it[0].sender_lat)
                            bundle.putDouble("sender_long", it[0].sender_long)
                            bundle.putDouble("receiver_lat", it[0].receiver_lat)
                            bundle.putDouble("receiver_long", it[0].receiver_long)
                            bundle.putDouble("earning", it[0].earning)
                            bundle.putDouble("distance", it[0].distance)
                            bundle.putInt("quantity", it[0].qty)
                            bundle.putInt("type", it[0].type)
                            fragment.arguments = bundle
                            communicatorFragmentInterface!!.addContentFragment(fragment, true)
                        }
                    }
                    status = 1
                })

                viewModel.deleteOrder.observe(viewLifecycleOwner, {
                    if (it.sender_area == PreferenceProvider(requireActivity()).getSharedPreferences("area_id")?.toInt()) {
                        orderListArray.remove(it)
                    }
                    binding.counter.text = orderListArray.size.toString()
                })
            } else {
                viewModel.orders.removeObservers(viewLifecycleOwner)
                viewModel.order.removeObservers(viewLifecycleOwner)
                viewModel.deleteOrder.removeObservers(viewLifecycleOwner)
                binding.onlineOffline.text = "You're Offline"
                binding.pendingOrders.visibility = View.GONE
            }
        }

        binding.getOrderRequest.setOnClickListener {
            if (from.isNotEmpty()) {
                if (toID != 0) {
                    binding.to.setText("")
                    val fragment = ParcelRequestFragment()
                    val bundle = Bundle()
                    bundle.putInt("fromId", fromId)
                    bundle.putInt("toId", toID)
                    bundle.putString("context", "home")
                    bundle.putString("from", from)
                    bundle.putString("to", to)
                    fragment.arguments = bundle
                    communicatorFragmentInterface?.addContentFragment(fragment, true)
                    toID = 0
                } else {
                    binding.rootLayout.snackbar("Please set a destination location")
                }
            } else {
                binding.rootLayout.snackbar("Please wait for pick up location.")
            }
        }
    }

    private fun getAPIClientInstance(): GoogleApiClient {
        return GoogleApiClient.Builder(requireActivity())
            .addApi(LocationServices.API).build()
    }

    private fun requestGPSSettings() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i("", "All location settings are satisfied.")
                    Toast.makeText(
                        requireActivity(),
                        "GPS is already enable",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "",
                        "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "
                    )
                    try {
                        status.startResolutionForResult(
                            requireActivity(),
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("Applicationsett", e.toString())
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.i(
                        "",
                        "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created."
                    )
                    Toast.makeText(
                        requireActivity(),
                        "Location settings are inadequate, and cannot be fixed here",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
                            this@HomeFragment
                        )
                        areaRecycler.adapter = myRecyclerViewAdapter
                    }
                } else {
                    areaRecycler.visibility = View.VISIBLE
                    noArea.visibility = View.GONE
                    val myRecyclerViewAdapter = AreaListRecyclerViewAdapter(
                        areaList,
                        this@HomeFragment
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
        } else {
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
            R.id.my_wallet -> {
                communicatorFragmentInterface?.addContentFragment(MyWalletFragment(), true)
                binding.drawerLayout.closeDrawers()
            }
            R.id.my_live_delivery -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(
                    MyLiveDeliveryFragment(),
                    true
                )
            }
//            R.id.preferred_area_order_list -> {
//                binding.drawerLayout.closeDrawers()
//                communicatorFragmentInterface?.addContentFragment(
//                    PreferredOrderListFragment(),
//                    true
//                )
//            }
            R.id.preferred_area -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(PreferredAreaFragment(), true)
            }
            R.id.my_delivery_history -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(
                    MyDeliveryHistoryFragment(),
                    true
                )
            }
            R.id.profile -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(ProfileFragment(), true)
            }
            R.id.help -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(HelpFragment(), true)
            }
            R.id.terms_condition -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(TermsConditionsFragment(), true)
            }
            R.id.sign_out -> {
                binding.drawerLayout.closeDrawers()
                dialogs = Dialog(requireActivity())
                dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogs.setContentView(R.layout.sign_out_dialog)
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
                    setProgressDialog(requireActivity())
                    lifecycleScope.launch {
                        val myAreaResponse = viewModel.viewMyArea()
                        if (myAreaResponse.success){
                            if (myAreaResponse.data.isNotEmpty()){
                                for (i in myAreaResponse.data.indices){
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(myAreaResponse.data[i].area_name)
                                }
                                val deleteFcmTokenResponse = viewModel.deleteFcmToken()
                                if (deleteFcmTokenResponse.success) {
                                    dismissDialog()
                                    Toast.makeText(requireActivity(), deleteFcmTokenResponse.msg, Toast.LENGTH_LONG).show()
                                    PersistentUser.getInstance().logOut(context)
                                    val intent = Intent(requireContext(), AuthActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                }
                            }
                        }

                    }
                }
                dialogs.setCancelable(false)
                dialogs.show()
            }
        }

        return true
    }

    override fun onItemClick(area: AreaListDataModelData) {
        dialogs.dismiss()
        if (currentLocationClick) {
            binding.from.setText(area.area_name)
            fromId = area.id
            from = area.area_name
        } else {
            binding.to.setText(area.area_name)
            toID = area.id
            to = area.area_name
        }
    }

    private fun turnOnGPS() {
        gpsDialog!!.setContentView(R.layout.gps_dialog)
        gpsDialog!!.findViewById<TextView>(R.id.turn_on).setOnClickListener {
            requestGPSSettings()
        }
        gpsDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        gpsDialog!!.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        gpsDialog!!.setCancelable(false)
        gpsDialog!!.show()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}