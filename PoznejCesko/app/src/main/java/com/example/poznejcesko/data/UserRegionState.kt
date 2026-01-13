package com.example.poznejcesko.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_region_state",
    primaryKeys = ["userId", "regionId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Region::class,
            parentColumns = ["id"],
            childColumns = ["regionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserRegionState(
    val userId: Int,
    val regionId: Int,
    val isUnlocked: Boolean = false,
    val bestScore: Int = 0,
    val maxPoints: Int = 0
)
