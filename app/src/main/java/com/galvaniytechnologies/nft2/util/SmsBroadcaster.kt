package com.galvaniytechnologies.nft2.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.galvaniytechnologies.nft2.data.MessagePayload
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson

object SmsBroadcaster {

    const val CUSTOM_SMS_ACTION = "com.yourapp.sms.RECEIVE_CUSTOM"
    const val EXTRA_PAYLOAD = "extra_payload"
    const val EXTRA_HMAC = "extra_hmac"

    fun broadcastMessageIntent(context: Context, payload: MessagePayload, hmac: String) {
        val intent = Intent(CUSTOM_SMS_ACTION).apply {
            // Ensure the intent is explicit if targeting a specific package
            // For now, we assume the SMS App will handle this implicit intent
            // If the SMS App has a specific package name, it should be set here:
            // setPackage("com.yourapp.sms") // Replace with actual SMS App package name
            putExtra(EXTRA_PAYLOAD, Gson().toJson(payload))
            putExtra(EXTRA_HMAC, hmac)
        }
        context.sendBroadcast(intent)
    }

    suspend fun broadcastMessageHttp(payload: MessagePayload, hmac: String) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
        }

        try {
            val response = client.post("http://localhost:8080/broadcast") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("payload" to payload, "hmac" to hmac))
            }
            Log.d("SmsBroadcaster", "HTTP Broadcast Response: ${response.status}")
        } catch (e: Exception) {
            Log.e("SmsBroadcaster", "HTTP Broadcast failed: ${e.message}")
        } finally {
            client.close()
        }
    }
}