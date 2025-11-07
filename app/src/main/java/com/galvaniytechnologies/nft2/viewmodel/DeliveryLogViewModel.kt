package com.galvaniytechnologies.nft2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.galvaniytechnologies.nft2.data.AppDatabase
import com.galvaniytechnologies.nft2.data.DeliveryLog
import com.galvaniytechnologies.nft2.data.DeliveryLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class DeliveryLogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DeliveryLogRepository
    val allLogs: Flow<List<DeliveryLog>>

    init {
        val deliveryLogDao = AppDatabase.getDatabase(application).deliveryLogDao()
        repository = DeliveryLogRepository(deliveryLogDao)
        allLogs = repository.allLogs
    }

    fun insertLog(
        recipients: List<String>,
        message: String,
        deliveryMethod: String,
        status: String,
        errorMessage: String? = null
    ) {
        val log = DeliveryLog(
            messageId = UUID.randomUUID().toString(),
            recipients = recipients.joinToString(","),
            message = message,
            timestamp = System.currentTimeMillis(),
            deliveryMethod = deliveryMethod,
            status = status,
            errorMessage = errorMessage
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(log)
        }
    }

    fun getLogsByStatus(status: String): Flow<List<DeliveryLog>> {
        return repository.getLogsByStatus(status)
    }

    fun deleteOldLogs(olderThan: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteOldLogs(olderThan)
        }
    }
}