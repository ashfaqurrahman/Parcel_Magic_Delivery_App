package com.airposted.bitoronbd_deliveryman.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.airposted.bitoronbd_deliveryman.BuildConfig
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentHomeBinding
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
        binding.navigationView.menu.findItem(R.id.version).isEnabled = false
        binding.navigationView.menu.findItem(R.id.version1).isEnabled = false
        binding.versionName.text = "Version " + BuildConfig.VERSION_NAME
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.my_wallet -> {
//                communicatorFragmentInterface?.addContentFragment(ProductFragment(), true)
                binding.drawerLayout.closeDrawers()
            }
            R.id.my_live_delivery -> {

                binding.drawerLayout.closeDrawers()
            }
            R.id.preferred_area -> {

                binding.drawerLayout.closeDrawers()
            }
            R.id.my_delivery_history -> {

                binding.drawerLayout.closeDrawers()
            }
            R.id.profile -> {

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
            }
            R.id.sign_out -> {

                binding.drawerLayout.closeDrawers()
            }
        }

        return true
    }
}