package com.mikeschvedov.whatshouldiwatch.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import javax.inject.Inject

class AirplaneModeChangedReciever @Inject constructor(): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val isAirplaneModeEnabled = intent?.getBooleanExtra("state", false) ?: return
        if(isAirplaneModeEnabled){
            Toast.makeText(context, "Airplane mode is enabled", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Airplane mode is disabled", Toast.LENGTH_SHORT).show()
        }
    }
}