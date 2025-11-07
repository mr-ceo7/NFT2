package com.galvaniytechnologies.nft2.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BroadcastingService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BroadcastingService", "Service started")
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}