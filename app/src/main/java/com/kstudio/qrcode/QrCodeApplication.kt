package com.kstudio.qrcode

import android.app.Application
import com.kstudio.qrcode.di.appModule
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
    }
}