package com.airposted.bitoronbd_deliveryman

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.os.Build
import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.NetworkConnectionInterceptor
import com.airposted.bitoronbd_deliveryman.data.network.preferences.PreferenceProvider
import com.airposted.bitoronbd_deliveryman.data.repositories.*
import com.airposted.bitoronbd_deliveryman.view.auth.AuthViewModelFactory
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import java.util.*

class Application : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@Application))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { HomeRepository(instance(), MyApi(instance()))}
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }


    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "Promotions",
                NotificationManager.IMPORTANCE_HIGH
            )

            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan1)

            //channel1.setSound(sound, attributes)

            val manager = getSystemService(
                NotificationManager::class.java
            )
            Objects.requireNonNull(manager)
                .createNotificationChannel(channel1)
        }
    }

    companion object {
        const val CHANNEL_1_ID = "Promotions"
    }



}