package com.example.komplexnejsiapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.komplexnejsiapp.databinding.FragmentSavedSchemesBinding
import com.google.android.material.snackbar.Snackbar

class SavedSchemesFragment : Fragment() {

    private var _binding: FragmentSavedSchemesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var colorAdapter: ColorAdapter
    private val savedColors = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedSchemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("ColorSchemes", Context.MODE_PRIVATE)

        setupRecyclerView()
        loadColors()
    }

    private fun setupRecyclerView() {
        colorAdapter = ColorAdapter(savedColors) { color ->
            deleteColor(color)
        }
        binding.recyclerView.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadColors() {
        val colors = sharedPreferences.getStringSet("saved_colors", emptySet())?.toMutableList() ?: mutableListOf()
        savedColors.clear()
        savedColors.addAll(colors)
        colorAdapter.notifyDataSetChanged()
    }

    private fun deleteColor(color: String) {
        val position = savedColors.indexOf(color)
        savedColors.remove(color)
        colorAdapter.notifyItemRemoved(position)

        Snackbar.make(binding.root, "Color deleted", Snackbar.LENGTH_LONG)
            .setAction("VRÁTIT") {
                savedColors.add(position, color)
                colorAdapter.notifyItemInserted(position)
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event != DISMISS_EVENT_ACTION) {
                        val currentColors = sharedPreferences.getStringSet("saved_colors", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                        currentColors.remove(color)
                        with(sharedPreferences.edit()) {
                            putStringSet("saved_colors", currentColors)
                            apply()
                        }
                    }
                }
            })
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadColors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}