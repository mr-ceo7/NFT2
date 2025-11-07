package com.galvaniytechnologies.NTFY.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.galvaniytechnologies.NTFY.viewmodel.DeliveryLogViewModel
import androidx.core.app.NotificationCompat
import com.galvaniytechnologies.NTFY.R
import com.galvaniytechnologies.NTFY.data.MessagePayload
import com.galvaniytechnologies.NTFY.util.SmsBroadcaster
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.BackoffPolicy
import java.util.concurrent.TimeUnit
import com.galvaniytechnologies.NTFY.worker.BroadcastWorker

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
            val viewModel = DeliveryLogViewModel(application)
            
            serviceScope.launch {
                try {
                    when (broadcastType) {
                        "intent" -> {
                            SmsBroadcaster.broadcastMessageIntent(applicationContext, payload, hmac)
                            Log.d("BroadcastingService", "Message broadcast via Intent.")
                            viewModel.insertLog(
                                recipients = payload.recipients,
                                message = payload.message,
                                deliveryMethod = "intent",
                                status = "sent"
                            )
                        }
                        "http" -> {
                            SmsBroadcaster.broadcastMessageHttp(payload, hmac)
                            Log.d("BroadcastingService", "Message broadcast via HTTP.")
                            viewModel.insertLog(
                                recipients = payload.recipients,
                                message = payload.message,
                                deliveryMethod = "http",
                                status = "sent"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BroadcastingService", "Broadcast failed: ${e.message}. Enqueuing retry.")
                    viewModel.insertLog(
                        recipients = payload.recipients,
                        message = payload.message,
                        deliveryMethod = broadcastType ?: "unknown",
                        status = "failed",
                        errorMessage = e.message
                    )
                    val workRequest = OneTimeWorkRequestBuilder<BroadcastWorker>()
                        .setInputData(androidx.work.Data.Builder()
                            .putString(SmsBroadcaster.EXTRA_PAYLOAD, payloadJson)
                            .putString(SmsBroadcaster.EXTRA_HMAC, hmac)
                            .putString("broadcast_type", broadcastType)
                            .build())
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                        .build()
                    WorkManager.getInstance(applicationContext).enqueue(workRequest)
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