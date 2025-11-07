package com.galvaniytechnologies.NTFY.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.galvaniytechnologies.NTFY.service.BroadcastingService
import com.galvaniytechnologies.NTFY.util.SmsBroadcaster

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received alarm broadcast")
        
        // Get the extras from the alarm Intent
        val payloadJson = intent.getStringExtra(SmsBroadcaster.EXTRA_PAYLOAD)
        val hmac = intent.getStringExtra(SmsBroadcaster.EXTRA_HMAC)
        val broadcastType = intent.getStringExtra("broadcast_type")

        // Create service intent with the same extras
        val serviceIntent = Intent(context, BroadcastingService::class.java).apply {
            putExtra(SmsBroadcaster.EXTRA_PAYLOAD, payloadJson)
            putExtra(SmsBroadcaster.EXTRA_HMAC, hmac)
            putExtra("broadcast_type", broadcastType)
        }

        // Start the service as foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        private const val TAG = "AlarmReceiver"
    }
}