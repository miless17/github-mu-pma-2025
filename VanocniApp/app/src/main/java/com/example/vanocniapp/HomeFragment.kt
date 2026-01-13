package com.example.vanocniapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vanocniapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeManager = StoreManager(requireContext().applicationContext)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(storeManager) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val adapter = GiftAdapter { id -> viewModel.removeGiftItem(id) }
        binding.recyclerGifts.layoutManager = LinearLayoutManager(context)
        binding.recyclerGifts.adapter = adapter

        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding.buttonAdd.setOnClickListener {
            val name = binding.editGiftName.text.toString()
            val price = binding.editGiftPrice.text.toString().toDoubleOrNull() ?: 0.0
            if (name.isNotBlank() && price > 0) {
                viewModel.addGiftItem(name, price)
                binding.editGiftName.text?.clear()
                binding.editGiftPrice.text?.clear()
            }
        }

        // Sledování dat z ViewModelu
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userName.collect { name ->
                        binding.textGreeting.text = if (name.isBlank()) "Ahoj!" else "Ahoj $name!"
                    }
                }
                launch {
                    viewModel.budgetLimit.collect { limit ->
                        binding.textLimit.text = "Limit: $limit Kč"
                        updateProgress()
                    }
                }
                launch {
                    viewModel.currentSpent.collect { spent ->
                        binding.textSpent.text = "Utraceno: $spent Kč"
                        updateProgress()
                    }
                }
                launch {
                    viewModel.giftItems.collect { items ->
                        adapter.submitList(items)
                    }
                }
            }
        }
    }

    private fun updateProgress() {
        val limit = viewModel.budgetLimit.value
        val spent = viewModel.currentSpent.value
        val remaining = limit - spent

        binding.textRemaining.text = "Zbývá: $remaining Kč"
        binding.progressBudget.max = if (limit > 0) limit.toInt() else 100
        binding.progressBudget.progress = spent.toInt()

        if (remaining < 0) {
            binding.textRemaining.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        } else {
            binding.textRemaining.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
