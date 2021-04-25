package com.airposted.bitoronbd_deliveryman.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aapbd.appbajarlib.storage.PersistentUser
import com.airposted.bitoronbd_deliveryman.view.auth.AuthActivity
import com.airposted.bitoronbd_deliveryman.view.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this

        if (PersistentUser.getInstance().isLogged(context)) {
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(context, AuthActivity::class.java))
            finish()
        }
    }
}