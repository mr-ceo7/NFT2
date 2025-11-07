package com.galvaniytechnologies.NTFY.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.galvaniytechnologies.NTFY.data.MessagePayload
import com.galvaniytechnologies.NTFY.receiver.AlarmReceiver
import com.galvaniytechnologies.NTFY.service.BroadcastingService
import com.google.gson.Gson

object AlarmScheduler {
    fun scheduleBroadcast(
        context: Context,
        payload: MessagePayload,
        hmac: String,
        broadcastType: String,
        triggerTime: Long
    ) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, BroadcastingService::class.java)

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            // Create an intent specifically for the broadcast receiver
            // Create a new broadcast intent
            val broadcastIntent = Intent(context, AlarmReceiver::class.java)
            broadcastIntent.putExtra(SmsBroadcaster.EXTRA_PAYLOAD, Gson().toJson(payload))
            broadcastIntent.putExtra(SmsBroadcaster.EXTRA_HMAC, hmac)
            broadcastIntent.putExtra("broadcast_type", broadcastType)

            // Always use getBroadcast with our AlarmReceiver for better reliability across OEMs
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                payload.hashCode(),
                broadcastIntent,
                flags
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }

            Log.d("AlarmScheduler", "Scheduled broadcast for ${payload.message} at $triggerTime")
        } catch (e: Exception) {
            // Catch any runtime exceptions (SecurityException, IllegalArgumentException, etc.) and log them
            Log.e("AlarmScheduler", "Failed to schedule broadcast: ${e.message}", e)
            // Re-throw to allow caller to handle if needed, or swallow to avoid crash at alarm scheduling time.
            throw e
        }
    }

    fun cancelScheduledBroadcast(
        context: Context,
        payload: MessagePayload,
        hmac: String,
        broadcastType: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BroadcastingService::class.java).apply {
            putExtra(SmsBroadcaster.EXTRA_PAYLOAD, Gson().toJson(payload))
            putExtra(SmsBroadcaster.EXTRA_HMAC, hmac)
            putExtra("broadcast_type", broadcastType)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getService(
            context,
            payload.hashCode(),
            intent,
            flags
        )

        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Cancelled scheduled broadcast for ${payload.message}")
    }
}