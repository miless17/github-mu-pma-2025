package com.example.poznejcesko

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.poznejcesko.data.User

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as PoznejCeskoApplication).repository)
    }
    
    private lateinit var currentUserTextView: TextView
    private lateinit var changeUserButton: Button
    private var isDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        currentUserTextView = findViewById(R.id.currentUserTextView)
        changeUserButton = findViewById(R.id.changeUserButton)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RegionAdapter { region ->
            val currentUser = viewModel.currentUser.value
            if (currentUser == null) {
                Toast.makeText(this, "Nejdříve vyberte hráče!", Toast.LENGTH_SHORT).show()
                showUserSelectionDialog()
            } else {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra(QuizActivity.EXTRA_REGION_ID, region.id)
                intent.putExtra(QuizActivity.EXTRA_USER_ID, currentUser.id)
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allRegions.observe(this) { regions ->
            regions?.let { adapter.submitList(it) }
        }
        
        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                currentUserTextView.text = "Hráč: ${user.name}"
            } else {
                currentUserTextView.text = "Hráč: Nikdo"
            }
        }
        
        // Automatické zobrazení dialogu při startu, pokud není nikdo vybrán
        viewModel.allUsers.observe(this) { users ->
             if (viewModel.currentUser.value == null && !isDialogOpen) {
                 if (users.isEmpty()) {
                     showCreateUserDialog()
                 } else {
                     showUserSelectionDialog()
                 }
             }
        }

        changeUserButton.setOnClickListener {
            showUserSelectionDialog()
        }
    }
    
    private fun showUserSelectionDialog() {
        if (isDialogOpen) return
        val users = viewModel.allUsers.value ?: emptyList()
        val userNames = users.map { it.name }.toMutableList()
        userNames.add("Nový uživatel...")

        isDialogOpen = true
        AlertDialog.Builder(this)
            .setTitle("Vyberte hráče")
            .setItems(userNames.toTypedArray()) { _, which ->
                isDialogOpen = false
                if (which < users.size) {
                    viewModel.setCurrentUser(users[which])
                } else {
                    showCreateUserDialog()
                }
            }
            .setCancelable(false) // Nutí uživatele k výběru při startu
            .show()
    }

    private fun showCreateUserDialog() {
        val input = EditText(this)
        isDialogOpen = true
        AlertDialog.Builder(this)
            .setTitle("Nový uživatel")
            .setMessage("Zadejte jméno:")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                isDialogOpen = false
                val name = input.text.toString()
                if (name.isNotBlank()) {
                    viewModel.createNewUser(name)
                } else {
                    Toast.makeText(this, "Jméno nesmí být prázdné", Toast.LENGTH_SHORT).show()
                    showUserSelectionDialog()
                }
            }
            .setNegativeButton("Zrušit") { _, _ ->
                isDialogOpen = false
                // Pokud uživatel zruší a nikdo není vybrán, vrátíme se na výběr
                if (viewModel.currentUser.value == null) {
                    showUserSelectionDialog()
                }
            }
            .setCancelable(false)
            .show()
    }
}
