package com.example.myapp004moreactivities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second2)

        val twInformation = findViewById<TextView>(R.id.twInformation)
        val btnThirdAct = findViewById<Button>(R.id.btnThirdAct)

    }
}