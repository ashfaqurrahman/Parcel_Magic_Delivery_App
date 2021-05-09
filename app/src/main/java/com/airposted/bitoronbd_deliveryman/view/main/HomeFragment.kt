package com.airposted.bitoronbd_deliveryman.view.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.airposted.bitoronbd_deliveryman.model.AreaListDataModelData
import com.airposted.bitoronbd_deliveryman.utils.*
import com.airposted.bitoronbd_deliveryman.view.auth.AuthActivity
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

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
        setProgressDialog(requireActivity())
        lifecycleScope.launch {
            try {
                val response = viewModel.getAllAreaList()
                areaList = response.data!!
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

        val hView: View = binding.navigationView.getHeaderView(0)
        val pic = hView.findViewById<CircleImageView>(R.id.profile_image)
        val name = hView.findViewById<TextView>(R.id.user_name)
        Glide.with(requireActivity()).load(
            PersistentUser.getInstance().getUserImage(requireActivity())
        ).placeholder(R.mipmap.ic_launcher).error(
            R.drawable.sample_pro_pic
        ).into(pic)
        name.text = PersistentUser.getInstance().getFullName(requireActivity())

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
            if (fromId != 0) {
                if (toID != 0) {
                    binding.from.setText("")
                    binding.to.setText("")
                    val fragment = ParcelRequestFragment()
                    val bundle = Bundle()
                    bundle.putInt("fromId", fromId)
                    bundle.putInt("toId", toID)
                    bundle.putString("from", from)
                    bundle.putString("to", to)
                    fragment.arguments = bundle
                    communicatorFragmentInterface?.addContentFragment(fragment, true)
                } else {
                    binding.rootLayout.snackbar("Please set a destination location")
                }
            } else {
                binding.rootLayout.snackbar("Please set a pick up location.")
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
//            R.id.my_wallet -> {
//                communicatorFragmentInterface?.addContentFragment(MyWalletFragment(), true)
//                binding.drawerLayout.closeDrawers()
//            }
            R.id.my_live_delivery -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(
                    MyLiveDeliveryFragment(),
                    true
                )
            }
            R.id.preferred_area_order_list -> {
                binding.drawerLayout.closeDrawers()
                communicatorFragmentInterface?.addContentFragment(
                    PreferredOrderListFragment(),
                    true
                )
            }
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
                    PersistentUser.getInstance().logOut(context)
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
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

    override fun onBackPressed(): Boolean {
        return false
    }
}