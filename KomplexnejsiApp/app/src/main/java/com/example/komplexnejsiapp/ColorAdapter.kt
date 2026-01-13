package com.example.komplexnejsiapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.komplexnejsiapp.databinding.ListItemColorBinding

class ColorAdapter(
    private val colors: MutableList<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ListItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]
        holder.bind(color)
    }

    override fun getItemCount(): Int = colors.size

    fun removeColor(color: String) {
        val position = colors.indexOf(color)
        if (position > -1) {
            colors.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addColor(color: String, position: Int) {
        colors.add(position, color)
        notifyItemInserted(position)
    }

    inner class ColorViewHolder(private val binding: ListItemColorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(colorHex: String) {
            binding.hexCodeText.text = colorHex
            binding.colorSwatch.setBackgroundColor(Color.parseColor(colorHex))
            binding.deleteButton.setOnClickListener { onDeleteClick(colorHex) }
        }
    }
}