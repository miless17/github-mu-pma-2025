package com.example.myapp010hadanicisla

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp010hadanicisla.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var secretNumber = 0
    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generateSecretNumber()

        binding.buttonCheck.setOnClickListener {
            checkGuess()
        }
    }

    private fun generateSecretNumber() {
        secretNumber = (1..100).random()
        // Toast.makeText(this, "Nové číslo je $secretNumber", Toast.LENGTH_SHORT).show() // Pro ladění
    }

    private fun checkGuess() {
        val guess = binding.editTextNumInput.text.toString().toIntOrNull()

        if (guess == null) {
            binding.textViewResult.text = "Zadej platné číslo."
            return
        }
        attempts++
        when {
            guess < secretNumber -> {
                binding.textViewResult.text = "Více! Zkus to znovu."
            }
            guess > secretNumber -> {
                binding.textViewResult.text = "Méně! Zkus to znovu."
            }
            else -> {
                binding.textViewResult.text = "Správně! Počet pokusů: $attempts"
                generateSecretNumber()
                attempts = 0
            }
        }
        binding.editTextNumInput.text.clear()
    }
}