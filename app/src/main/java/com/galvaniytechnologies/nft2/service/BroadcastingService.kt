package com.galvaniytechnologies.nft2.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.galvaniytechnologies.nft2.R
import com.galvaniytechnologies.nft2.data.MessagePayload
import com.galvaniytechnologies.nft2.util.SmsBroadcaster
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BroadcastingService : Service() {

    private val CHANNEL_ID = "BroadcastingServiceChannel"
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SMS Broadcaster")
            .setContentText("Broadcasting messages...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
            .build()

        startForeground(1, notification)

        val payloadJson = intent?.getStringExtra(SmsBroadcaster.EXTRA_PAYLOAD)
        val hmac = intent?.getStringExtra(SmsBroadcaster.EXTRA_HMAC)
        val broadcastType = intent?.getStringExtra("broadcast_type")

        if (payloadJson != null && hmac != null) {
            val payload = Gson().fromJson(payloadJson, MessagePayload::class.java)
            serviceScope.launch {
                when (broadcastType) {
                    "intent" -> {
                        SmsBroadcaster.broadcastMessageIntent(applicationContext, payload, hmac)
                        Log.d("BroadcastingService", "Message broadcast via Intent.")
                    }
                    "http" -> {
                        SmsBroadcaster.broadcastMessageHttp(payload, hmac)
                        Log.d("BroadcastingService", "Message broadcast via HTTP.")
                    }
                }
                stopSelf()
            }
        } else {
            Log.e("BroadcastingService", "Missing payload or HMAC in intent.")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Broadcasting Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}