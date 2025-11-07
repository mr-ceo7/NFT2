package com.galvaniytechnologies.nft2.data

import kotlinx.coroutines.flow.Flow

class DeliveryLogRepository(private val deliveryLogDao: DeliveryLogDao) {
    val allLogs: Flow<List<DeliveryLog>> = deliveryLogDao.getAllLogs()

    suspend fun insert(log: DeliveryLog) {
        deliveryLogDao.insert(log)
    }

    fun getLogsByStatus(status: String): Flow<List<DeliveryLog>> {
        return deliveryLogDao.getLogsByStatus(status)
    }

    suspend fun deleteOldLogs(timestamp: Long) {
        deliveryLogDao.deleteOldLogs(timestamp)
    }
}