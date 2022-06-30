package com.mikeschvedov.whatshouldiwatch.utils.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*


class UploadWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    companion object {
        const val KEY_WORKER = "key_worker"
    }

    override fun doWork(): Result {

        // ---⬇⬇⬇⬇⬇⬇⬇--- DO MAIN WORK HERE ---⬇⬇⬇⬇⬇⬇⬇--- //
        try {
            // Getting the data we passed into the worker
            val count = inputData.getInt("KEY_COUNT_VALUE",0)

            for(i in 0 until count){
                println("Uploading $i")

            }

            // output data from within the worker
            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentData = time.format(Date())

            val outPutData = Data.Builder()
                .putString(KEY_WORKER, currentData)
            .build()

            // send the data without regulat result
            return Result.success(outPutData)

        } catch (e: Exception) {
            return Result.failure()
        }

    }
}