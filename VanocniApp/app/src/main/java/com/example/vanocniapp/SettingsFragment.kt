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
import com.example.vanocniapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
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
        // Změna: ViewModel je nyní vázán na Activity, aby přežil popBackStack()
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        // Načtení aktuálních hodnot do polí
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userName.collect { name ->
                        if (binding.editUserName.text.isNullOrEmpty()) {
                            binding.editUserName.setText(name)
                        }
                    }
                }
                launch {
                    viewModel.budgetLimit.collect { limit ->
                        if (binding.editBudgetLimit.text.isNullOrEmpty()) {
                            binding.editBudgetLimit.setText(if (limit == 0.0) "" else limit.toString())
                        }
                    }
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.editUserName.text.toString()
            val limit = binding.editBudgetLimit.text.toString().toDoubleOrNull() ?: 0.0
            
            viewModel.saveUserName(name)
            viewModel.saveBudgetLimit(limit)
            
            findNavController().popBackStack()
        }

        binding.buttonClearAll.setOnClickListener {
            viewModel.clearAllGifts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
