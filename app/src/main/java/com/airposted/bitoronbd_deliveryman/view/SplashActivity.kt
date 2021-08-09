package com.airposted.bitoronbd_deliveryman.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.aapbd.appbajarlib.storage.PersistData
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.utils.AppHelper
import com.airposted.bitoronbd_deliveryman.view.auth.AuthActivity
import com.airposted.bitoronbd_deliveryman.view.main.MainActivity
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SplashActivity : AppCompatActivity() {

    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this

        if (PersistData.getBooleanData(context, AppHelper.OPEN_SCREEN_LOAD)) {
            if (PersistentUser.getInstance().isLogged(context)) {
                if (checkPermissions()) {
                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(context, PermissionActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(context, AuthActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(context, IntroActivity::class.java))
            finish()
        }
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
}