package com.example.komplexnejsiapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.komplexnejsiapp.databinding.FragmentGeneratorBinding
import java.util.Random

class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private var currentColor: Int = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("ColorSchemes", Context.MODE_PRIVATE)

        currentColor = sharedPreferences.getInt("lastColor", Color.BLACK)
        setNewColor(currentColor)

        binding.generateButton.setOnClickListener {
            val newColor = generateRandomColor()
            setNewColor(newColor)
            saveLastColor(newColor)
        }

        binding.copyButton.setOnClickListener {
            copyToClipboard(binding.hexCodeText.text.toString())
        }

        binding.saveButton.setOnClickListener {
            saveColor(currentColor)
        }
    }

    private fun generateRandomColor(): Int {
        val random = Random()
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    private fun setNewColor(color: Int) {
        currentColor = color
        binding.colorView.setBackgroundColor(color)
        binding.hexCodeText.text = String.format("#%06X", 0xFFFFFF and color)
    }

    private fun saveLastColor(color: Int) {
        with(requireActivity().getPreferences(Context.MODE_PRIVATE).edit()) {
            putInt("lastColor", color)
            apply()
        }
    }

    private fun saveColor(color: Int) {
        val hexColor = String.format("#%06X", 0xFFFFFF and color)
        val savedColors = sharedPreferences.getStringSet("saved_colors", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if (savedColors.add(hexColor)) {
            with(sharedPreferences.edit()) {
                putStringSet("saved_colors", savedColors)
                apply()
            }
            Toast.makeText(requireContext(), "Barvička uložena!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Barva už v seznamu existuje!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Zkopírováno!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}