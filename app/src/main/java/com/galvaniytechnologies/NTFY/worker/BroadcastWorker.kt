package com.galvaniytechnologies.NTFY.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.galvaniytechnologies.NTFY.data.MessagePayload
import com.galvaniytechnologies.NTFY.util.SmsBroadcaster
import com.google.gson.Gson

class BroadcastWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val payloadJson = inputData.getString(SmsBroadcaster.EXTRA_PAYLOAD)
        val hmac = inputData.getString(SmsBroadcaster.EXTRA_HMAC)
        val broadcastType = inputData.getString("broadcast_type")

        if (payloadJson == null || hmac == null || broadcastType == null) {
            Log.e("BroadcastWorker", "Missing input data for broadcast.")
            return Result.failure()
        }

        val payload = Gson().fromJson(payloadJson, MessagePayload::class.java)

        return try {
            when (broadcastType) {
                "intent" -> {
                    SmsBroadcaster.broadcastMessageIntent(applicationContext, payload, hmac)
                    Log.d("BroadcastWorker", "Message broadcast via Intent (retry).")
                }
                "http" -> {
                    SmsBroadcaster.broadcastMessageHttp(payload, hmac)
                    Log.d("BroadcastWorker", "Message broadcast via HTTP (retry).")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("BroadcastWorker", "Broadcast failed (retry): ${e.message}")
            Result.retry()
        }
    }
}