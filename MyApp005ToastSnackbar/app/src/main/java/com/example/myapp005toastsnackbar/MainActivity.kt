package com.example.myapp005toastsnackbar

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp005toastsnackbar.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Nastavení akce pro tlačítko Toast
        binding.btnShowToast.setOnClickListener {
            val toast = Toast.makeText(this, "Ahoj Toaste!", Toast.LENGTH_SHORT)
            toast.show()
        }

        //Nastavení akce pro tlačítko Snackbar
        binding.btnShowSnackbar.setOnClickListener {
            Snackbar.make(binding.root, "Jsem víc než jenom TOAST", Snackbar.LENGTH_SHORT)
            //val snackbar = Snackbar.make(binding.root, "Ahoj Snackbaru!", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor("#FFCC50"))
                .setTextColor(Color.BLACK)
                .setDuration(7000)
                .setActionTextColor(Color.WHITE)
                .setAction("Zavřít") {
                    Toast.makeText(this, "Zavírám SNACKBAR!!", Toast.LENGTH_SHORT).show()
                }
            .show()

        }
    }
}