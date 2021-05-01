package com.airposted.bitoronbd_deliveryman

import android.app.Application
import com.airposted.bitoronbd_deliveryman.data.network.MyApi
import com.airposted.bitoronbd_deliveryman.data.network.NetworkConnectionInterceptor
import com.airposted.bitoronbd_deliveryman.data.network.preferences.PreferenceProvider
import com.airposted.bitoronbd_deliveryman.data.repositories.*
import com.airposted.bitoronbd_deliveryman.view.auth.AuthViewModelFactory
import com.airposted.bitoronbd_deliveryman.view.main.HomeViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

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



}