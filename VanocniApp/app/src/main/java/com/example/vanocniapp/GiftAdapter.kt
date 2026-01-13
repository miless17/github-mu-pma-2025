package com.example.vanocniapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vanocniapp.databinding.ItemGiftBinding

class GiftAdapter(private val onDeleteClick: (String) -> Unit) :
    ListAdapter<GiftItem, GiftAdapter.GiftViewHolder>(GiftDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftViewHolder {
        val binding = ItemGiftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GiftViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GiftViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GiftViewHolder(private val binding: ItemGiftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GiftItem) {
            binding.textGiftName.text = item.name
            binding.textGiftPrice.text = "${item.price} Kč"
            binding.buttonDelete.setOnClickListener { onDeleteClick(item.id) }
        }
    }

    class GiftDiffCallback : DiffUtil.ItemCallback<GiftItem>() {
        override fun areItemsTheSame(oldItem: GiftItem, newItem: GiftItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GiftItem, newItem: GiftItem): Boolean {
            return oldItem == newItem
        }
    }
}
