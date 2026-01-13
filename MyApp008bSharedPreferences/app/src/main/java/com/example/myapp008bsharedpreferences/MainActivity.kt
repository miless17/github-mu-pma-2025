package com.example.myapp008bsharedpreferences

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp008bsharedpreferences.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        val editor = sharedPref.edit()

        //funkce ukládání dat do sharedPreferences
        binding.btnSave.setOnClickListener {
            val jmeno = binding.etJmeno.text.toString()
            val vek = binding.etVek.text.toString()

            if (vek.isBlank()) {
                Toast.makeText(this, "Vyplňte věk", Toast.LENGTH_SHORT).show()
            } else {
                val vekNum = vek.toInt()
                val isadult = binding.checkBox.isChecked
                if ((vekNum < 18 && isadult) || (vekNum >= 18 && !isadult)) {
                    Toast.makeText(this, "To nevypadá legitimně", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Uloženo", Toast.LENGTH_SHORT).show()
                    editor.apply {
                        putString("jmeno", jmeno)
                        putInt("vek", vekNum)
                        putBoolean("isAdult", isadult)
                        apply()
                    }
                }
            }
        }


        //funkce na načítání dat z sharedPreferences
        binding.btnLoad.setOnClickListener {
            val jmeno = sharedPref.getString("jmeno", null)
            val vek = sharedPref.getInt("vek", 0)
            val isAdult = sharedPref.getBoolean("isAdult", false)

            binding.etJmeno.setText(jmeno)
            binding.etVek.setText(vek.toString())
            binding.checkBox.isChecked = isAdult
        }

    }
}