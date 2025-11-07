package com.galvaniytechnologies.nft2.data

data class MessagePayload(
    val recipients: List<String>,
    val message: String,
    val timestamp: Long,
    val sender: String = "BroadcasterApp"
)