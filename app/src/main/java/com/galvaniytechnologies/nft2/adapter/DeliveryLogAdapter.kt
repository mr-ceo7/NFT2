package com.galvaniytechnologies.nft2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.galvaniytechnologies.nft2.data.DeliveryLog
import com.galvaniytechnologies.nft2.databinding.ItemDeliveryLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeliveryLogAdapter : ListAdapter<DeliveryLog, DeliveryLogAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemDeliveryLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(log: DeliveryLog) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            binding.textViewTimestamp.text = dateFormat.format(Date(log.timestamp))
            binding.textViewMessage.text = log.message
            binding.textViewRecipients.text = "To: ${log.recipients}"
            binding.textViewStatus.text = "Status: ${log.status} (${log.deliveryMethod})"
            binding.textViewError.text = log.errorMessage ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeliveryLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffCallback : DiffUtil.ItemCallback<DeliveryLog>() {
        override fun areItemsTheSame(oldItem: DeliveryLog, newItem: DeliveryLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeliveryLog, newItem: DeliveryLog): Boolean {
            return oldItem == newItem
        }
    }
}