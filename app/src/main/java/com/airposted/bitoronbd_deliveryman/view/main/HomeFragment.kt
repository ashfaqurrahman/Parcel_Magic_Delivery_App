package com.airposted.bitoronbd_deliveryman.view.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.BuildConfig
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentHomeBinding
import com.airposted.bitoronbd_deliveryman.view.auth.AuthActivity
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: FragmentHomeBinding
    var communicatorFragmentInterface: CommunicatorFragmentInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.versionName.text = "Version " + BuildConfig.VERSION_NAME
        communicatorFragmentInterface = context as CommunicatorFragmentInterface

        binding.getOrderRequest.setOnClickListener {
            val fragment = ParcelRequestFragment()
            val bundle = Bundle()
            bundle.putString("from", binding.searchFrom.query.toString())
            bundle.putString("to", binding.searchTo.query.toString())
            fragment.arguments = bundle
            communicatorFragmentInterface?.addContentFragment(fragment, true)
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
//            R.id.my_wallet -> {
//                communicatorFragmentInterface?.addContentFragment(MyWalletFragment(), true)
//                binding.drawerLayout.closeDrawers()
//            }
            R.id.my_live_delivery -> {
                communicatorFragmentInterface?.addContentFragment(MyLiveDeliveryFragment(), true)
                binding.drawerLayout.closeDrawers()
            }
            R.id.all_parcel_request -> {
                communicatorFragmentInterface?.addContentFragment(AllParcelRequestFragment(), true)
                binding.drawerLayout.closeDrawers()
            }
            R.id.preferred_area -> {
                communicatorFragmentInterface?.addContentFragment(PreferredAreaFragment(), true)
                binding.drawerLayout.closeDrawers()
            }
            R.id.my_delivery_history -> {
                communicatorFragmentInterface?.addContentFragment(MyDeliveryHistoryFragment(), true)
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
                bundle.putString("url", "https://airposted.com/page/terms-of-service#:~:text=Airposted%20Referral%20Program%20Terms%20and%20Conditions%2A%20Airposted%20Referral,it%20as%20a%20traveler%20or%20shopper%20or%20buyer.")
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
}