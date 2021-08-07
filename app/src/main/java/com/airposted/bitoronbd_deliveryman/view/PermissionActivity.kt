package com.airposted.bitoronbd_deliveryman.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.ActivityPermissionBinding
import com.airposted.bitoronbd_deliveryman.view.main.MainActivity
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.ArrayList

class PermissionActivity : AppCompatActivity(), KodeinAware {
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
    private val REQUEST_CHECK_SETTINGS = 0x1
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var permissionBinding: ActivityPermissionBinding
    private lateinit var viewModel: HomeViewModel
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionBinding = DataBindingUtil.setContentView(this, R.layout.activity_permission)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        setupUI()
    }

    private fun setupUI() {
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)

        permissionBinding.currentLocation.setOnClickListener {
            Permissions.check(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        googleApiClient = getAPIClientInstance()
                        googleApiClient.connect()

                        viewModel.gps.observe(this@PermissionActivity, {
                            if (it) {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)

                            } else {
                                requestGPSSettings()
                            }
                        })
                    }

                    override fun onDenied(
                        context: Context?,
                        deniedPermissions: ArrayList<String>?
                    ) {
                        super.onDenied(context, deniedPermissions)
                    }

                    override fun onBlocked(
                        context: Context?,
                        blockedList: ArrayList<String>?
                    ): Boolean {
                        return super.onBlocked(context, blockedList)
                    }
                })
        }
    }

    private fun getAPIClientInstance(): GoogleApiClient {
        return GoogleApiClient.Builder(this)
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
                        this,
                        "GPS is already enable",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "",
                        "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "
                    )
                    try {
                        status.startResolutionForResult(
                            this,
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
                        this,
                        "Location settings are inadequate, and cannot be fixed here",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}