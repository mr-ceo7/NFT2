package com.galvaniytechnologies.nft2.util

import android.content.Context
import android.util.Log
// import com.google.android.gms.nearby.Nearby
// import com.google.android.gms.nearby.connection.AdvertisingCallback
// import com.google.android.gms.nearby.connection.AdvertisingOptions
// import com.google.android.gms.nearby.connection.ConnectionInfo
// import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
// import com.google.android.gms.nearby.connection.ConnectionResolution
// import com.google.android.gms.nearby.connection.ConnectionsClient
// import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
// import com.google.android.gms.nearby.connection.Payload
// import com.google.android.gms.nearby.connection.PayloadCallback
// import com.google.android.gms.nearby.connection.PayloadTransferUpdate
// import com.google.android.gms.nearby.connection.Strategy
// import com.google.android.gms.tasks.Task

class NearbyConnectionsManager(private val context: Context) {

    private val SERVICE_ID = "com.galvaniytechnologies.nft2.BROADCASTER"
    // private val STRATEGY = Strategy.P2P_CLUSTER

    // private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)

    // private val advertisingCallbacks = object : AdvertisingCallback() {
    //     override fun onAdvertisingStarted() {
    //         Log.d("NearbyConnectionsManager", "Advertising started")
    //     }

    //     override fun onAdvertisingFailed(reason: Int) {
    //         Log.e("NearbyConnectionsManager", "Advertising failed: $reason")
    //     }
    // }

    // private val connectionLifecycleCallbacks = object : ConnectionLifecycleCallback() {
    //     override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
    //         Log.d("NearbyConnectionsManager", "Connection initiated with ${connectionInfo.endpointName}")
    //         connectionsClient.acceptConnection(endpointId, object : PayloadCallback() {
    //             override fun onPayloadReceived(endpointId: String, payload: Payload) {
    //                 // Handle received payload (e.g., SMS App sending status back)
    //                 Log.d("NearbyConnectionsManager", "Payload received from $endpointId")
    //             }

    //             override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
    //                 // Payload transfer update
    //             }
    //         })
    //     }

    //     override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
    //         when (result.status.statusCode) {
    //             ConnectionsStatusCodes.STATUS_OK -> {
    //                 Log.d("NearbyConnectionsManager", "Connected to $endpointId")
    //             }
    //             ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
    //                 Log.d("NearbyConnectionsManager", "Connection rejected by $endpointId")
    //             }
    //             ConnectionsStatusCodes.STATUS_ERROR -> {
    //                 Log.e("NearbyConnectionsManager", "Connection error with $endpointId")
    //             }
    //             else -> {
    //                 Log.e("NearbyConnectionsManager", "Unknown connection result with $endpointId: ${result.status.statusCode}")
    //             }
    //         }
    //     }

    //     override fun onDisconnected(endpointId: String) {
    //         Log.d("NearbyConnectionsManager", "Disconnected from $endpointId")
    //     }
    // }

    fun startAdvertising(endpointName: String) {
        Log.d("NearbyConnectionsManager", "Nearby Connections advertising is commented out.")
        // connectionsClient.startAdvertising(
        //     endpointName,
        //     SERVICE_ID,
        //     advertisingCallbacks,
        //     connectionLifecycleCallbacks,
        //     AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        // ).addOnSuccessListener {
        //     Log.d("NearbyConnectionsManager", "Started advertising as $endpointName")
        // }.addOnFailureListener {
        //     Log.e("NearbyConnectionsManager", "Failed to start advertising: ${it.message}")
        // }
    }

    fun stopAdvertising() {
        Log.d("NearbyConnectionsManager", "Nearby Connections advertising stop is commented out.")
        // connectionsClient.stopAdvertising()
        // Log.d("NearbyConnectionsManager", "Stopped advertising")
    }

    fun sendPayload(endpointId: String, payload: ByteArray) {
        Log.d("NearbyConnectionsManager", "Nearby Connections sendPayload is commented out.")
        // connectionsClient.sendPayload(endpointId, Payload.fromBytes(payload))
        //     .addOnSuccessListener {
        //         Log.d("NearbyConnectionsManager", "Payload sent to $endpointId")
        //     }.addOnFailureListener {
        //         Log.e("NearbyConnectionsManager", "Failed to send payload to $endpointId: ${it.message}")
        //     }
    }
}