package com.galvaniytechnologies.nft2.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.galvaniytechnologies.nft2.data.MessagePayload
import com.galvaniytechnologies.nft2.service.BroadcastingService
import com.google.gson.Gson

object AlarmScheduler {
    fun scheduleBroadcast(
        context: Context,
        payload: MessagePayload,
        hmac: String,
        broadcastType: String,
        triggerTime: Long
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