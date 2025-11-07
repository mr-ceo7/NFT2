package com.galvaniytechnologies.NTFY

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import com.galvaniytechnologies.NTFY.data.MessagePayload
import com.galvaniytechnologies.NTFY.util.HmacUtil
import com.galvaniytechnologies.NTFY.util.SmsBroadcaster
import com.google.gson.Gson
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.galvaniytechnologies.NTFY.service.BroadcastingService
import com.galvaniytechnologies.NTFY.databinding.FragmentFirstBinding
import com.galvaniytechnologies.NTFY.util.AlarmScheduler
import com.galvaniytechnologies.NTFY.viewmodel.DeliveryLogViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.broadcastButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            val recipients = binding.recipientsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

            if (message.isEmpty() || recipients.isEmpty()) {
                Toast.makeText(requireContext(), "Message and recipients cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val timestamp = System.currentTimeMillis()
            val payload = MessagePayload(recipients, message, timestamp)

            // Convert payload to JSON string (using Gson for simplicity, will add dependency later if needed)
            val gson = Gson()
            val payloadJson = gson.toJson(payload)

            val hmac = HmacUtil.generateHmac(payloadJson)

            Log.d("BroadcasterApp", "Payload JSON: $payloadJson")
            Log.d("BroadcasterApp", "HMAC: $hmac")

            val serviceIntent = Intent(requireContext(), BroadcastingService::class.java).apply {
                putExtra(SmsBroadcaster.EXTRA_PAYLOAD, payloadJson)
                putExtra(SmsBroadcaster.EXTRA_HMAC, hmac)
                putExtra("broadcast_type", "intent")
            }
            requireContext().startForegroundService(serviceIntent)

            Toast.makeText(requireContext(), "Message broadcast via Intent.", Toast.LENGTH_LONG).show()
        }

        binding.broadcastHttpButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            val recipients = binding.recipientsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

            if (message.isEmpty() || recipients.isEmpty()) {
                Toast.makeText(requireContext(), "Message and recipients cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val timestamp = System.currentTimeMillis()
            val payload = MessagePayload(recipients, message, timestamp)

            val gson = Gson()
            val payloadJson = gson.toJson(payload)

            val hmac = HmacUtil.generateHmac(payloadJson)

            Log.d("BroadcasterApp", "HTTP Payload JSON: $payloadJson")
            Log.d("BroadcasterApp", "HTTP HMAC: $hmac")

            val serviceIntent = Intent(requireContext(), BroadcastingService::class.java).apply {
                putExtra(SmsBroadcaster.EXTRA_PAYLOAD, payloadJson)
                putExtra(SmsBroadcaster.EXTRA_HMAC, hmac)
                putExtra("broadcast_type", "http")
            }
            requireContext().startForegroundService(serviceIntent)

            Toast.makeText(requireContext(), "Message broadcast via HTTP.", Toast.LENGTH_LONG).show()
        }

        binding.scheduleBroadcastButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            val recipients = binding.recipientsEditText.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val scheduleTimeStr = binding.scheduleTimeEditText.text.toString()

            if (message.isEmpty() || recipients.isEmpty() || scheduleTimeStr.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Message, recipients, and schedule time cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val scheduleTimeMinutes = scheduleTimeStr.toLongOrNull()
            if (scheduleTimeMinutes == null || scheduleTimeMinutes <= 0) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid schedule time in minutes",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val triggerTime = System.currentTimeMillis() + (scheduleTimeMinutes * 60 * 1000)
            val payload = MessagePayload(recipients, message, triggerTime)
            val payloadJson = Gson().toJson(payload)
            val hmac = HmacUtil.generateHmac(payloadJson)

            // Schedule via AlarmManager (defensive: catch errors and show a toast instead of crashing)
            try {
                AlarmScheduler.scheduleBroadcast(
                    requireContext(),
                    payload,
                    hmac,
                    "intent", // Default to intent-based broadcast for scheduled messages
                    triggerTime
                )

                Toast.makeText(
                    requireContext(),
                    "Message scheduled for broadcast in $scheduleTimeMinutes minutes",
                    Toast.LENGTH_LONG
                ).show()

                // Log the scheduled broadcast
                val viewModel = DeliveryLogViewModel(requireActivity().application)
                viewModel.insertLog(
                    recipients = recipients,
                    message = message,
                    deliveryMethod = "scheduled_intent",
                    status = "scheduled"
                )
            } catch (e: Exception) {
                Log.e("FirstFragment", "Failed to schedule broadcast: ${e.message}", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to schedule broadcast: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}