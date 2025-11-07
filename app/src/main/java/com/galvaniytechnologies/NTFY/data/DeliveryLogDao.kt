package com.galvaniytechnologies.NTFY.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryLogDao {
    @Insert
    suspend fun insert(log: DeliveryLog)

    @Query("SELECT * FROM delivery_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<DeliveryLog>>

    @Query("SELECT * FROM delivery_logs WHERE status = :status ORDER BY timestamp DESC")
    fun getLogsByStatus(status: String): Flow<List<DeliveryLog>>

    @Query("DELETE FROM delivery_logs WHERE timestamp < :timestamp")
    suspend fun deleteOldLogs(timestamp: Long)
}