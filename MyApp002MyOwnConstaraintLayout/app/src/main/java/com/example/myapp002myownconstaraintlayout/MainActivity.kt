package com.example.myapp002myownconstaraintlayout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etNickname = findViewById<EditText>(R.id.etNickname)
        val etAge = findViewById<EditText>(R.id.etAge)
        val tvInformation = findViewById<TextView>(R.id.tvInformation)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        // Nastavení obsluhy pro tlačítko odeslat

        btnSend.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val nickname = etNickname.text.toString()
            val age = etAge.text.toString()

            // Zobrazení textu v TextView
            val formatedText = "Jmenuji se $name $surname a je mi $age let. Moje přezdívka je $nickname."

            tvInformation.text = formatedText

        }

        btnDelete.setOnClickListener {
            tvInformation.text = ""
            etName.text.clear()
            etSurname.text.clear()
            etAge.text.clear()
            etNickname.text.clear()

        }

    }
}