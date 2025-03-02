package com.kstudio.qrcode.di

import android.app.Application
import com.kstudio.qrcode.ScanDatabase
import com.kstudio.qrcode.features.history.data.ScanHistoryRepository
import com.kstudio.qrcode.features.history.data.ScanHistoryRepositoryImpl
import com.kstudio.qrcode.features.history.data.datasource.ScanHistoryDao
import com.kstudio.qrcode.features.scan.CameraPreviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun provideDatabase(application: Application): ScanDatabase = ScanDatabase.getDatabase(application)

fun provideHistoryDao(scanDatabase: ScanDatabase): ScanHistoryDao = scanDatabase.historyDao()

val appModule = module {
    single { provideDatabase(get()) }
    single { provideHistoryDao(get()) }
    factory<ScanHistoryRepository> { ScanHistoryRepositoryImpl(get()) }
    viewModelOf(::CameraPreviewViewModel)
}