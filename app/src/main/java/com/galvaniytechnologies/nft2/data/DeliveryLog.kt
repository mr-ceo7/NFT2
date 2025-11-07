package com.galvaniytechnologies.nft2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery_logs")
data class DeliveryLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val messageId: String,
    val recipients: String, // Comma-separated list
    val message: String,
    val timestamp: Long,
    val deliveryMethod: String, // "intent" or "http"
    val status: String, // "sent", "failed", "retrying"
    val errorMessage: String? = null
)