package com.example.myapp009imagetoapp

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp009imagetoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentRotation = 0f
    private var isFiltered = false

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        binding.imageViewObrazek.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonObrazek.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.buttonOtocit.setOnClickListener {
            currentRotation += 90f
            binding.imageViewObrazek.rotation = currentRotation
        }

        binding.buttonFiltr.setOnClickListener {
            isFiltered = !isFiltered
            if (isFiltered) {
                val matrix = ColorMatrix()
                matrix.setSaturation(0f) // Grayscale
                val filter = ColorMatrixColorFilter(matrix)
                binding.imageViewObrazek.colorFilter = filter
            } else {
                binding.imageViewObrazek.colorFilter = null
            }
        }
    }
}
