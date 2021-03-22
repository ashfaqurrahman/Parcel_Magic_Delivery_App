package com.airposted.bitoronbd_deliveryman.ui.main

import android.os.Bundle
import android.system.Os.close
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var drawerLayout: DrawerLayout? = null
    var navController: NavController? = null
    var navigationView: NavigationView? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupNavigation()
    }

    // Setting Up One Time Navigation
    private fun setupNavigation() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)

//        val toggle = ActionBarDrawerToggle(
//            this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close
//        )
//        toggle.isDrawerIndicatorEnabled = false
//        toggle.setHomeAsUpIndicator(R.drawable.ic_menu)
//        toggle.setToolbarNavigationClickListener {
//            binding.drawerLayout.openDrawer(GravityCompat.START)
//        }


        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController!!, drawerLayout)
        NavigationUI.setupWithNavController(navigationView!!, navController!!)
        navigationView!!.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout)
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        drawerLayout!!.closeDrawers()
        when (menuItem.itemId) {
            R.id.first -> navController?.navigate(R.id.fragment_my_wallet)
            R.id.second -> navController?.navigate(R.id.fragment_my_live_delivery)
            R.id.third -> navController?.navigate(R.id.fragment_all_parcel_request)
        }
        return true
    }
}