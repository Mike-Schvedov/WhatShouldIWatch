package com.mikeschvedov.whatshouldiwatch.utils.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*


class FilteringWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        // ---⬇⬇⬇⬇⬇⬇⬇--- DO MAIN WORK HERE ---⬇⬇⬇⬇⬇⬇⬇--- //
        return try {
            for(i in 0..300){
                println("Filtering $i")
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }

    }
}