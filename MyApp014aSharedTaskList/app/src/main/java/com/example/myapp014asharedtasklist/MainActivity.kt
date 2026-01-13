package com.example.myapp014asharedtasklist

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp014asharedtasklist.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        adapter = TaskAdapter(
            tasks = emptyList(),
            onChecked = { task -> toggleCompleted(task) },
            onDelete = { task -> deleteTask(task) },
            onEdit = { task -> showEditDialog(task) } // Přidáno volání dialogu
        )

        binding.recyclerViewTasks.adapter = adapter
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        binding.buttonAdd.setOnClickListener {
            val title = binding.inputTask.text.toString()
            if (title.isNotEmpty()) {
                addTask(title)
                binding.inputTask.text.clear()
            }
        }

        listenForTasks()
    }

    private fun addTask(title: String) {
        val task = Task(title = title, completed = false)
        db.collection("tasks").add(task)
    }

    private fun toggleCompleted(task: Task) {
        if (task.id.isEmpty()) return
        db.collection("tasks").document(task.id)
            .update("completed", !task.completed)
    }

    private fun deleteTask(task: Task) {
        if (task.id.isEmpty()) return
        db.collection("tasks").document(task.id).delete()
    }

    // Nová funkce pro zobrazení editačního dialogu
    private fun showEditDialog(task: Task) {
        val editText = EditText(this)
        editText.setText(task.title)

        AlertDialog.Builder(this)
            .setTitle("Upravit úkol")
            .setView(editText)
            .setPositiveButton("Uložit") { _, _ ->
                val newTitle = editText.text.toString()
                if (newTitle.isNotEmpty() && newTitle != task.title) {
                    updateTaskTitle(task.id, newTitle)
                }
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }

    private fun updateTaskTitle(taskId: String, newTitle: String) {
        if (taskId.isEmpty()) return
        db.collection("tasks").document(taskId)
            .update("title", newTitle)
    }

    private fun listenForTasks() {
        db.collection("tasks")
            .addSnapshotListener { snapshots, _ ->
                val taskList = mutableListOf<Task>()
                snapshots?.let {
                    for (doc in it) {
                        val task = doc.toObject(Task::class.java)
                        task.id = doc.id
                        taskList.add(task)
                    }
                }
                adapter.submitList(taskList)
            }
    }
}
