package com.airposted.bitoronbd_deliveryman.view.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.ActivityMainBinding
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeFragment
import com.airposted.bitoronbd_deliveryman.view.main.live_parcel.MyLiveDeliveryFragment


class MainActivity : AppCompatActivity(), CommunicatorFragmentInterface {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val bundle = intent.extras
        if (bundle != null) {
            val data = bundle.get("category")
            Log.e("aaaaaa", data.toString())
            //bundle must contain all info sent in "data" field of the notification
        }

        if (intent.getStringExtra("notification") != null){
            addContentFragment(MyLiveDeliveryFragment(), true)
        } else {
            addContentFragment(HomeFragment(), false)
        }
    }

    override fun addContentFragment(fragment: Fragment?, addToBackStack: Boolean) {
        if (fragment == null) {
            return
        }
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frameLayout)

        if (currentFragment != null && fragment.javaClass.isAssignableFrom(currentFragment.javaClass)) {
            return
        }

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, fragment, fragment.javaClass.name)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.name)
        }
        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.frameLayout)
        (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
            super.onBackPressed()
        }
    }
}