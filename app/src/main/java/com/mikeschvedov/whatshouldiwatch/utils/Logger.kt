package com.mikeschvedov.whatshouldiwatch.utils

import android.util.Log
import com.mikeschvedov.whatshouldiwatch.BuildConfig

class Logger {
    companion object{
        fun i(tag: String, message: String){
            if(BuildConfig.DEBUG) Log.e(tag, message)
        }
        fun e(tag: String, message: String){
            if(BuildConfig.DEBUG) Log.e(tag, message)
        }
    }
}