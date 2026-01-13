package com.example.poznejcesko.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "regions")
data class Region(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val requiredScoreToUnlock: Int,
    val order: Int 
)

data class RegionWithState(
    val id: Int,
    val name: String,
    val requiredScoreToUnlock: Int,
    val order: Int,
    val isLocked: Boolean,
    val bestScore: Int = 0,
    val maxPoints: Int = 0
)
