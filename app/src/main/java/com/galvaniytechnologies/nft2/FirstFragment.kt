package com.galvaniytechnologies.nft2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import com.galvaniytechnologies.nft2.data.MessagePayload
import com.galvaniytechnologies.nft2.util.HmacUtil
import com.galvaniytechnologies.nft2.util.SmsBroadcaster
import com.google.gson.Gson
import android.content.Intent
import com.galvaniytechnologies.nft2.service.BroadcastingService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.galvaniytechnologies.nft2.databinding.FragmentFirstBinding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}