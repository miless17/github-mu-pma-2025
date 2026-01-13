package com.example.myapp014asharedtasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp014asharedtasklist.databinding.ItemTaskBinding

class TaskAdapter(
    private var tasks: List<Task>,
    private val onChecked: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val onEdit: (Task) -> Unit // Nový callback pro editaci
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.binding.textTitle.text = task.title
        
        // Důležité: vypnout listener před nastavením isChecked, aby se nespustil cyklus
        holder.binding.checkCompleted.setOnCheckedChangeListener(null)
        holder.binding.checkCompleted.isChecked = task.completed

        holder.binding.checkCompleted.setOnClickListener {
            onChecked(task)
        }

        // Kliknutí na text vyvolá editaci
        holder.binding.textTitle.setOnClickListener {
            onEdit(task)
        }

        holder.binding.imageDelete.setOnClickListener {
            onDelete(task)
        }
    }

    override fun getItemCount() = tasks.size

    fun submitList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged()
    }
}
