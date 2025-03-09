package com.kstudio.qrcode

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.kstudio.qrcode.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class QrCodeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@QrCodeApplication)
            androidLogger()
            modules(appModule)
        }

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        val adConfig = RequestConfiguration
            .Builder()
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .build()

        backgroundScope.launch {
            MobileAds.setRequestConfiguration(adConfig)
            MobileAds.initialize(this@QrCodeApplication) {
            }
        }
        AppOpenAdManager(this, this.getString(R.string.ad_id_app_open))
    }
}