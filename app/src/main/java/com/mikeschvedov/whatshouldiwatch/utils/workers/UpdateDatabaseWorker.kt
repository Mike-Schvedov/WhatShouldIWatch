package com.mikeschvedov.whatshouldiwatch.utils.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mikeschvedov.whatshouldiwatch.data.repository.ContentMediator
import com.mikeschvedov.whatshouldiwatch.utils.Logger
import com.mikeschvedov.whatshouldiwatch.utils.workers.UploadWorker.Companion.KEY_WORKER
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject





class UpdateDatabaseWorker  constructor(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val UPDATE_DB_TIME = "update_time"
    }

    @Inject
    lateinit var contentMediator: ContentMediator

    override suspend fun doWork(): Result {

        return try {
            // This suspended method can be run inside this suspended version of worker (CoroutineWorker)
            contentMediator.updateDatabaseViaApi()

            // output data from within the worker
            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentData = time.format(Date())

            val outPutData = Data.Builder()
                .putString(UPDATE_DB_TIME, currentData)
                .build()

            Result.success(outPutData)
        } catch (e: Exception) {
            Logger.i("WorkerUpdate Error: ", e.message ?: "No Data")
            Result.failure()
        }
    }
}