package com.example.poznejcesko

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.poznejcesko.data.RegionWithState
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

class RegionAdapter(private val onRegionClicked: (RegionWithState) -> Unit) :
    ListAdapter<RegionWithState, RegionAdapter.RegionViewHolder>(RegionsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_region, parent, false)
        return RegionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            if (!current.isLocked) {
                onRegionClicked(current)
            }
        }
    }

    class RegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val regionNameView: TextView = itemView.findViewById(R.id.regionName)
        private val lockIconView: ImageView = itemView.findViewById(R.id.lockIcon)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.regionProgress)
        private val starsLayout: LinearLayout = itemView.findViewById(R.id.starsLayout)
        private val regionIcon: ImageView = itemView.findViewById(R.id.regionIcon)

        fun bind(region: RegionWithState) {
            regionNameView.text = region.name
            
            if (region.isLocked) {
                lockIconView.visibility = View.VISIBLE
                lockIconView.setImageResource(android.R.drawable.ic_lock_lock)
                cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"))
                regionNameView.setTextColor(Color.GRAY)
                progressBar.visibility = View.GONE
                starsLayout.visibility = View.GONE
                regionIcon.imageTintList = ColorStateList.valueOf(Color.GRAY)
            } else {
                lockIconView.visibility = View.GONE
                cardView.setCardBackgroundColor(Color.WHITE)
                regionNameView.setTextColor(Color.parseColor("#333333"))
                progressBar.visibility = View.VISIBLE
                starsLayout.visibility = View.VISIBLE
                regionIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#6200EE"))

                val progressPercent = if (region.maxPoints > 0) {
                    (region.bestScore.toFloat() / region.maxPoints * 100).toInt()
                } else 0
                
                progressBar.progress = progressPercent
                
                // NOVÁ LOGIKA HVĚZD PRO MENU:
                val starCount = when (region.bestScore) {
                    50 -> 3
                    in 30..40 -> 2
                    in 10..20 -> 1
                    else -> 0
                }
                
                updateStars(starCount)
                
                if (progressPercent == 100) {
                    cardView.strokeWidth = 4
                    cardView.strokeColor = Color.parseColor("#FFD700")
                } else {
                    cardView.strokeWidth = 0
                }
            }
        }

        private fun updateStars(starCount: Int) {
            val goldColor = Color.parseColor("#FFD700")
            for (i in 0 until starsLayout.childCount) {
                val star = starsLayout.getChildAt(i) as ImageView
                if (i < starCount) {
                    star.setImageResource(android.R.drawable.btn_star_big_on)
                    star.imageTintList = ColorStateList.valueOf(goldColor)
                } else {
                    star.setImageResource(android.R.drawable.btn_star_big_off)
                    star.imageTintList = ColorStateList.valueOf(Color.LTGRAY)
                }
            }
        }
    }

    class RegionsComparator : DiffUtil.ItemCallback<RegionWithState>() {
        override fun areItemsTheSame(oldItem: RegionWithState, newItem: RegionWithState): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RegionWithState, newItem: RegionWithState): Boolean {
            return oldItem == newItem
        }
    }
}
