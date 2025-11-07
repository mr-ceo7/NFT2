package com.galvaniytechnologies.nft2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galvaniytechnologies.nft2.adapter.DeliveryLogAdapter
import com.galvaniytechnologies.nft2.databinding.FragmentDebugLogsBinding
import com.galvaniytechnologies.nft2.viewmodel.DeliveryLogViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DebugLogsFragment : Fragment() {
    private var _binding: FragmentDebugLogsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DeliveryLogViewModel
    private lateinit var adapter: DeliveryLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebugLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[DeliveryLogViewModel::class.java]
        
        adapter = DeliveryLogAdapter()
        binding.recyclerViewLogs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DebugLogsFragment.adapter
        }

        // Collect delivery logs
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allLogs.collectLatest { logs ->
                adapter.submitList(logs)
            }
        }

        // Delete old logs (older than 7 days)
        binding.buttonClearOldLogs.setOnClickListener {
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            viewModel.deleteOldLogs(sevenDaysAgo)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}