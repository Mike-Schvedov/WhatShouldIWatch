package com.mikeschvedov.whatshouldiwatch

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mikeschvedov.whatshouldiwatch.utils.Logger
import com.mikeschvedov.whatshouldiwatch.utils.workers.UpdateDatabaseWorker
import dagger.hilt.android.HiltAndroidApp
import java.security.acl.Owner
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class AppManager: Application() {

    override fun onCreate() {
        super.onCreate()
    }

}