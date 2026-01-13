package com.example.myapp012amynotehub.data

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp012amynotehub.databinding.ActivityEditNoteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var noteDao: NoteDao
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDao = NoteHubDatabaseInstance.getDatabase(this).noteDao()
        noteId = intent.getIntExtra("note_id", -1)

        // Nastavení Spinneru
        val categories = listOf("Ostatní", "Škola", "Práce", "Osobní")
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEditCategory.adapter = adapter

        if (noteId != -1) {
            binding.tvNoteId.text = "ID: $noteId"
            lifecycleScope.launch(Dispatchers.IO) {
                val note = noteDao.getNoteById(noteId)
                if (note != null) {
                    withContext(Dispatchers.Main) {
                        binding.etEditTitle.setText(note.title)
                        binding.etEditContent.setText(note.content)
                        
                        // Nastavení vybrané kategorie
                        val categoryIndex = categories.indexOf(note.category)
                        if (categoryIndex >= 0) {
                            binding.spinnerEditCategory.setSelection(categoryIndex)
                        } else {
                            // Pokud kategorie není v seznamu (např. stará hodnota "General" vs "Ostatní"), 
                            // můžeme zkusit nastavit na 0 nebo najít nejbližší shodu.
                            // Pro tento případ dáme 0 ("Ostatní")
                             binding.spinnerEditCategory.setSelection(0)
                        }
                    }
                }
            }
        }

        binding.btnSaveChanges.setOnClickListener {
            val updatedTitle = binding.etEditTitle.text.toString()
            val updatedContent = binding.etEditContent.text.toString()
            val updatedCategory = binding.spinnerEditCategory.selectedItem.toString()

            val updatedNote = Note(
                id = noteId,
                title = updatedTitle,
                content = updatedContent,
                category = updatedCategory
            )

            lifecycleScope.launch(Dispatchers.IO) {
                noteDao.update(updatedNote)
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }
    }
}