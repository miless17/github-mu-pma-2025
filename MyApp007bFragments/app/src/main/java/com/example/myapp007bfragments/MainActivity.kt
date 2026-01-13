package com.example.myapp007bfragments

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.myapp007bfragments.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnFragment1.setOnClickListener {
            replaceFragment(Fragment1())
        }

        binding.btnFragment2.setOnClickListener {
            replaceFragment(Fragment2())
        }

        binding.btnFragment3.setOnClickListener {
            replaceFragment(Fragment3())
        }

        binding.btnFragment4.setOnClickListener {
            replaceFragment(Fragment4())
        }

        binding.btnExit.setOnClickListener {
            finish()
        }

    }
    private fun replaceFragment(fragment: Fragment) {
        //získání instance správce fragmentů
        val fragmentManager = supportFragmentManager
        //zapsání transakce
        val fragmentTransaction = fragmentManager.beginTransaction()

        //nahrazení fragmentu
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        //potvrzení transakce a provedení výměny fragmentu ve view
        fragmentTransaction.commit()

    }
}